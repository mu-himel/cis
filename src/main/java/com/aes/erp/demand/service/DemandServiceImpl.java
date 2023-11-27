package com.aes.erp.demand.service;

import com.aes.erp.authentication.JwtUtil;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.demand.dto.request.DemandReceiveDto;
import com.aes.erp.demand.dto.request.DemandRequestDto;
import com.aes.erp.demand.dto.request.ReviewDto;
import com.aes.erp.demand.dto.response.DemandDetailItemResDto;
import com.aes.erp.demand.dto.response.DemandDetailResDto;
import com.aes.erp.demand.entity.Demand;
import com.aes.erp.demand.entity.DemandDetail;
import com.aes.erp.demand.enums.DemandItemStatus;
import com.aes.erp.demand.enums.DemandStatus;
import com.aes.erp.demand.repository.DemandDetailRepository;
import com.aes.erp.demand.repository.DemandRepository;
import com.aes.erp.employee.entity.Employee;

import com.aes.erp.employee.service.EmployeeService;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.service.ItemService;
import com.aes.erp.module_access.approval_setting.repository.ApprovalSettingQuery;
import com.aes.erp.module_access.approval_setting.service.ApprovalSettingService;
import com.aes.erp.module_access.service.ModuleAccessPermissionService;
import com.aes.erp.verification.dto.response.Verifier;
import com.aes.erp.verification.entity.Comment;
import com.aes.erp.verification.entity.Verification;
import com.aes.erp.verification.enums.DomainType;
import com.aes.erp.verification.repository.VerificationRepository.VerificationResponse;
import com.aes.erp.verification.service.CommentService;
import com.aes.erp.verification.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DemandServiceImpl implements DemandService{

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private DemandDetailRepository demandDetailRepository;
    @Autowired
    private EmployeeService employeeService;


    @Autowired
    private ItemService itemService;

    @Autowired
    private ModuleAccessPermissionService moduleAccessPermissionService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ApprovalSettingService approvalSettingService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    @Transactional
    public void createDemand(String token, String uri, DemandRequestDto demandRequestDto) {
        Demand demand = demandRequestDto.getEntity();
        Long categoryId =   demandRequestDto.getCategory().getId();
        Long subCategoryId = demandRequestDto.getSubCategory().getId();

        //TODO  need to re-write get verifiers logic
        Optional<Map<String,Object>> verifierOp = (Optional<Map<String,Object>>)verificationService
                .getVerifiers(token,uri,categoryId,subCategoryId);

        List<Verifier> verifiers = new ArrayList<>();
        if(verifierOp.isPresent()){
            Map<String,Object> verification = verifierOp.get();
            verifiers = (List<Verifier>) verification.get("verifiers");
            Boolean verificationRequired = (Boolean) verification.get("verificationRequired");
            if(verificationRequired!=null && verificationRequired==true && verifiers!=null && verifiers.size()>0){
                demand.setStatus(DemandStatus.PENDING_VERIFICATION);
            }else{
                demand.setStatus(DemandStatus.PENDING);
            }

        }else{
            demand.setStatus(DemandStatus.PENDING);
        }

        demand.setDemandDate(LocalDateTime.now());
        demand.setCategory(new ItemCategory(demandRequestDto.getCategory().getId()));
        demand.setSubCategory(new ItemCategory(demandRequestDto.getSubCategory().getId()));
        demand.setRequestedBy(new Employee(demandRequestDto.getRequestedBy().getId()));
        setDemandDetail(demandRequestDto, demand);
        demandRepository.save(demand);

        List<ApprovalSettingQuery.ApprovalPanel> approvalPanels = approvalSettingService.getModuleWiseApprovalSetting(uri,
                Optional.of(categoryId),Optional.empty());

        setVerifiers(demand, verifiers);
        setApprovers(demand, approvalPanels);
    }

    private static void setDemandDetail(DemandRequestDto demandRequestDto, Demand demand) {
        demand.setDemandDetails(demandRequestDto.getDemandDetails().stream().map(demandDetailDto -> {
            DemandDetail demandDetail = new DemandDetail();
            demandDetail.setItem(new Item(demandDetailDto.getItem().getId()));
            if(demandDetailDto.getSubCategory()!=null) {
                demandDetail.setItemCategory(new ItemCategory(demandDetailDto.getSubCategory().getId()));
            }
            if(demandDetailDto.getCategory()!=null) {
                demandDetail.setItemParentCategory(new ItemCategory(demandDetailDto.getCategory().getId()));
            }

            demandDetail.setAttributes(demandDetailDto.getAttributes()
                    .stream().map(demandDetailAttribute -> {
                        demandDetailAttribute.setDemandDetail(demandDetail);
                        return demandDetailAttribute;
                    }).collect(Collectors.toList()));

            demandDetail.setRequestQuantity(demandDetailDto.getRequestQuantity());
            demandDetail.setDemand(demand);
            demandDetail.setPriority(demandDetailDto.getPriority());
            demandDetail.setSpecification(demandDetailDto.getSpecification());
            demandDetail.setStatus(demand.getStatus());
            return demandDetail;
        }).collect(Collectors.toList()));
    }

    private void setVerifiers(Demand demand, List<Verifier> verifiers) {
        if(verifiers.size()>0){
            Optional<Verifier> firstOp = verifiers.stream().findFirst();
            Verifier _verifier = firstOp.get();
            List<Verification> verifications = verifiers.stream().map(verifier -> {
                Verification verification = new Verification();
                verification.setDomainId(demand.getId());
                verification.setDomainType(DomainType.DEMAND);
                verification.setVerified(false);
                verification.setIsApproval(false);
                verification.setVerifier(new Employee(verifier.getId()));
                return verification;
            }).collect(Collectors.toList());
            demand.setNextVerifierId(_verifier.getId());
            verificationService.addVerification(verifications);
        }
    }

    private void setApprovers(Demand demand, List<ApprovalSettingQuery.ApprovalPanel> approvalPanels) {
        if(approvalPanels.size()>0){

            List<Verification> verifications = approvalPanels.stream().map(approvalPanel -> {
                Verification verification = new Verification();
                verification.setDomainId(demand.getId());
                verification.setDomainType(DomainType.DEMAND);
                verification.setVerified(false);
                verification.setIsApproval(true);
                verification.setVerifier(new Employee(approvalPanel.getId()));
                return verification;
            }).collect(Collectors.toList());
            verificationService.addVerification(verifications);
        }
    }

    @Override
    public Page<?> getMyDemands(Long id, Optional<Integer> page, Optional<Integer> size) {

        Optional<Employee> employeeOptional = employeeService.getEmployeeByUserId(id);

        if(employeeOptional.isEmpty()){
            throw new AesException("No Employee Profile Found");
        }
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return demandRepository.findAllByRequestedById(employeeOptional.get().getId(),pageable);
    }

    @Override
    public Page<?> getAllDemands(String token, Optional<Integer> page, Optional<Integer> size) {
        String moduleUri = "demand/pending";
        // get filter options according to module permission
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);

        Optional<Map<String,List<Long>>> modulePermission = moduleAccessPermissionService
                    .getModulePermissionFilterByUri(token,moduleUri);
        List<Long> categoryIds = new ArrayList<>();
        if(modulePermission.isPresent()){
            categoryIds = modulePermission.get().get("category_id");
            return demandRepository.findAllDemandsByCategory(categoryIds,pageable);
        }


        return demandRepository.findAllDemands(pageable);
    }

    @Override
    public Page<?> getAllPendingVerificationDemands(String token, Optional<Integer> page, Optional<Integer> size) {
        String moduleUri = "demand/pending-verification";
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        Optional<Map<String,List<Long>>> modulePermission = moduleAccessPermissionService
                .getModulePermissionFilterByUri(token,moduleUri);

        List<Long> categoryIds = new ArrayList<>();
        if(modulePermission.isPresent()){
            categoryIds = modulePermission.get().get("category_id");
            return demandRepository.findAllDemandsByCategoryAndDemandStatusAndNextVerifierId(categoryIds,
                    claimResponseDto.getEmployee().getId(),
                    DemandStatus.PENDING_VERIFICATION,pageable);
        }
        return demandRepository.findAllDemandsByDemandStatusAndNextVerifierId(DemandStatus.PENDING_VERIFICATION,
                claimResponseDto.getEmployee().getId(),
                pageable);
    }

    @Override
    public Page<?> getAllPendingApprovalDemands(String token, Optional<Integer> page, Optional<Integer> size) {
        String moduleUri = "demand/pending-approval";
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        Optional<Map<String,List<Long>>> modulePermission = moduleAccessPermissionService
                .getModulePermissionFilterByUri(token,moduleUri);

        List<Long> categoryIds = new ArrayList<>();
        if(modulePermission.isPresent()){
            categoryIds = modulePermission.get().get("category_id");
            return demandRepository.findAllDemandsByCategoryAndDemandStatusAndNextApproverId(categoryIds,
                    claimResponseDto.getEmployee().getId(),
                    DemandStatus.PENDING_APPROVAL,pageable);
        }
        return demandRepository.findAllDemandsByDemandStatusAndNextApproverId(DemandStatus.PENDING_APPROVAL,
                claimResponseDto.getEmployee().getId(),
                pageable);
    }

    @Override
    public Page<?> getAllCloseDemands(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return demandRepository.findAllCloseDemands(pageable);
    }

    @Override
    public Optional<?> getDemandDetail(Long id) {
        LocalDateTime localDateTime = LocalDateTime.now();
        String yearMonth = localDateTime.getYear()+"-"+((localDateTime.getMonthValue()<10)?
                    "0"+localDateTime.getMonthValue():localDateTime.getMonthValue());
        List<DemandRepository.DemandDetailItem> demandList = demandRepository.findByDemandId(id,yearMonth);
        DemandDetailResDto resDto = DemandDetailResDto.builder().build();
        for(DemandRepository.DemandDetailItem demandDetailItem: demandList){
            resDto.setDemandNo(demandDetailItem.getDemandNo());
            resDto.setDemandId(demandDetailItem.getDemandId());
            resDto.setDemandDate(demandDetailItem.getDemandDate());
            resDto.setDemandStatus(demandDetailItem.getDemandStatus());
            resDto.setEmpId(demandDetailItem.getEmpId());
            resDto.setEmployeeId(demandDetailItem.getEmployeeId());
            resDto.setEmployeeName(demandDetailItem.getEmployeeName());
            resDto.setReportingManager(demandDetailItem.getReportingManager());
            resDto.setDepartment(demandDetailItem.getDepartment());
            resDto.setDesignation(demandDetailItem.getDesignation());
            resDto.addDetail(
                DemandDetailItemResDto.builder()
                .itemId(demandDetailItem.getId())
                .itemCategoryId(demandDetailItem.getItemCategoryId())
                .itemParentCategoryId(demandDetailItem.getItemParentCategoryId())
                .category(demandDetailItem.getCategory())
                .categoryCode(demandDetailItem.getCategoryCode())
                .parentCategory(demandDetailItem.getParentCategory())
                .parentCategoryCode(demandDetailItem.getParentCategoryCode())
                .name(demandDetailItem.getName())
                .code(demandDetailItem.getCode())
                .demandDetailId(demandDetailItem.getDemandDetailId())
                .prQty(demandDetailItem.getPrQty())
                .demandItemStatus(demandDetailItem.getDemandDetailStatus())
                .requestedQuantity(demandDetailItem.getRequestQuantity())
                .stockThresholdQty(demandDetailItem.getStockThresholdQty())
                .specification(demandDetailItem.getSpecification())
                .currentStock(demandDetailItem.getCurrentStockQty())
                .approvedQuantity(demandDetailItem.getApprovedQuantity())
                .demandPriority(demandDetailItem.getDemandPriority())
                .itemUnit(demandDetailItem.getItemUnit())
                .totalStockInCurrentMonth(demandDetailItem.getTotalStockInCurrentMonth())
                .avgTotalConsumeInCurrentMonth(demandDetailItem.getAvgTotalConsumeInCurrentMonth())
                .totalConsumeInCurrentMonth(demandDetailItem.getTotalConsumeInCurrentMonth())
                .build()
            );

        }

        if(resDto.getDemandId()!=null) {
            List<VerificationResponse> verifiers = new ArrayList<>();
            List<VerificationResponse> approvers = new ArrayList<>();
            verificationService
                    .getVerificationsByDomainTypeAndDomainId(DomainType.DEMAND, resDto.getDemandId())
                    .stream().forEach(verifier->{
                        if(verifier.getIsApproval()==false){
                            verifiers.add(verifier);
                        }else{
                            approvers.add(verifier);
                        }
                    });
            resDto.setVerifiers(verifiers);
            resDto.setApprovers(approvers);
        }
        List<?> comments = commentService.getCommentsByDomain(DomainType.DEMAND, resDto.getDemandId());
        resDto.setComments(comments);
        return Optional.ofNullable(resDto);
    }

    @Override
    @Transactional
    public void receiveDemandItem(String token, DemandReceiveDto demandReceiveDto) {
        Optional<Demand> demandOptional = demandRepository.findById(demandReceiveDto.getDemandId());
        if(demandOptional.isEmpty()){
            throw new AesException("Demand not found");
        }

        if(demandReceiveDto.getNote()==null || demandReceiveDto.getNote().isEmpty()){
            throw new AesException("Note Required");
        }

        Demand demand = demandOptional.get();

        Integer completeCount  = demandDetailRepository.countByStatusAndDemandId(DemandStatus.COMPLETED,demand.getId());
        Integer demandCount = demand.getDemandDetails().size();

        demand.setDemandDetails(demand.getDemandDetails().stream().map(demandDetail -> {
            Long demandDetailId = demandDetail.getId();
            if(demandDetailId.equals(demandReceiveDto.getDemandDetailId())) {
                Item item = demandDetail.getItem();
                if(demandReceiveDto.getQty()!=null && demandDetail.getApprovedQuantity()>=demandReceiveDto.getQty()) {
                    itemService.stockOut(item, demandReceiveDto.getQty());
                }else{
                    itemService.stockOut(item,demandDetail.getApprovedQuantity());
                }
                if(demandReceiveDto.getNote()!=null && !demandReceiveDto.getNote().isEmpty()){
                    demandDetail.setReceiveNote(demandReceiveDto.getNote());
                }
                demandDetail.setStatus(DemandStatus.COMPLETED);
            }
           return demandDetail;
        }).collect(Collectors.toList()));

        if((demandCount-completeCount) == 1){
            demand.setStatus(DemandStatus.COMPLETED);
        }


    }

    @Override
    @Transactional
    public void declineDemandItem(String token, DemandReceiveDto demandReceiveDto) {
        Optional<Demand> demandOptional = demandRepository.findById(demandReceiveDto.getDemandId());
        if(demandOptional.isEmpty()){
            throw new AesException("Demand not found");
        }

        if(demandReceiveDto.getNote()==null || demandReceiveDto.getNote().isEmpty()){
            throw new AesException("Note Required");
        }

        Demand demand = demandOptional.get();

//        Integer completeCount  = demandDetailRepository.countByStatusAndDemandId(DemandItemStatus.COMPLETED,demand.getId());
//        Integer demandCount = demand.getDemandDetails().size();

        demand.setDemandDetails(demand.getDemandDetails().stream().map(demandDetail -> {
            Long demandDetailId = demandDetail.getId();
            if(demandDetailId.equals(demandReceiveDto.getDemandDetailId())) {

                if(demandReceiveDto.getNote()!=null && !demandReceiveDto.getNote().isEmpty()){
                    demandDetail.setReceiveNote(demandReceiveDto.getNote());
                }
                demandDetail.setStatus(DemandStatus.DECLINED);
            }
            return demandDetail;
        }).collect(Collectors.toList()));


    }

    @Override
    @Transactional
    public void sentDemandItem(DemandReceiveDto demandReceiveDto) {
        Optional<Demand> demandOptional = demandRepository.findById(demandReceiveDto.getDemandId());
        if(demandOptional.isEmpty()){
            throw new AesException("Demand not found");
        }
        Demand demand = demandOptional.get();
        Integer pendingQcCount  = demandDetailRepository.countByStatusAndDemandId(DemandStatus.PENDING_QC,demand.getId());

        System.out.println("Item Pending "+ pendingQcCount);

        Integer demandCount = demand.getDemandDetails().size();
        System.out.println("DItem Pending "+ demandCount);
        demand.setDemandDetails(demand.getDemandDetails().stream().map(demandDetail -> {
            Long demandDetailId = demandDetail.getId();
            if(demandDetailId.equals(demandReceiveDto.getDemandDetailId())) {
                demandDetail.setApprovedQuantity(demandReceiveDto.getQty());
                demandDetail.setStatus(DemandStatus.PENDING_QC);
                if(demandReceiveDto.getNote()!=null && !demandReceiveDto.getNote().isEmpty()){
                    demandDetail.setStoreNote(demandReceiveDto.getNote());
                }
            }
            return demandDetail;
        }).collect(Collectors.toList()));

        if((demandCount-pendingQcCount)>1){
            demand.setStatus(DemandStatus.PARTIAL);
        }else {
            demand.setStatus(DemandStatus.PENDING_QC);
        }
    }

    @Override
    @Transactional
    public void reviewDemand(String token, Long id, ReviewDto reviewDto) {
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Optional<Demand> demandOptional = demandRepository.findById(id);
        if(demandOptional.isEmpty()){
            throw new AesException("Demand not found");
        }
        Demand demand = demandOptional.get();
        demand.setReviewerId(null);
        demand.setStatus(DemandStatus.PENDING_VERIFICATION);
        commentService.addComment(commentService.prepareComment(
                claimResponseDto,
                reviewDto.getDomainType(),
                demand.getId(),
                reviewDto.getMessage()
        ));
    }

    @Override
    @Transactional
    public void rejectDemandItem(String token, DemandReceiveDto demandReceiveDto) {
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Optional<DemandDetail> demandDetailOp = demandDetailRepository
                                        .findById(demandReceiveDto.getDemandDetailId());
        if(demandDetailOp.isPresent()){
            DemandDetail demandDetail = demandDetailOp.get();

            if(demandReceiveDto.getNote()!=null && !demandReceiveDto.getNote().isEmpty()){

                commentService.addComment(commentService.prepareComment(claimResponseDto,
                        DomainType.DEMAND, demandDetail.getId(), demandReceiveDto.getNote()));
            }
            demandDetail.setStatus(DemandStatus.REJECTED);
        }
    }

    @Override
    @Transactional
    public void resendDemandItem(String token, DemandReceiveDto demandReceiveDto) {
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Optional<DemandDetail> demandDetailOp = demandDetailRepository
                .findById(demandReceiveDto.getDemandDetailId());
        if(demandDetailOp.isPresent()){
            DemandDetail demandDetail = demandDetailOp.get();

            if(demandReceiveDto.getNote()!=null && !demandReceiveDto.getNote().isEmpty()){

                commentService.addComment(commentService.prepareComment(claimResponseDto,
                        DomainType.DEMAND, demandDetail.getId(), demandReceiveDto.getNote()));
            }
            demandDetail.setStatus(DemandStatus.PENDING_QC);
        }
    }

    @Override
    @Transactional
    public void verifyComplete(Long id,Optional<VerificationResponse> firstApprover) {
        Optional<Demand> demandOp  = demandRepository.findById(id);
        if(demandOp.isPresent()){
            Demand demand = demandOp.get();
            if(firstApprover.isPresent()){
                demand.setNextApproverId(firstApprover.get().getVerifier().getId());
                demand.setStatus(DemandStatus.PENDING_APPROVAL);
                demand.setDemandDetails(
                        demand.getDemandDetails().stream().map(demandDetail -> {
                            demandDetail.setStatus(DemandStatus.PENDING_APPROVAL);
                            return demandDetail;
                        }).collect(Collectors.toList())
                );
            }else {
                demand.setStatus(DemandStatus.PENDING);
                demand.setDemandDetails(
                        demand.getDemandDetails().stream().map(demandDetail -> {
                            demandDetail.setStatus(DemandStatus.PENDING);
                            return demandDetail;
                        }).collect(Collectors.toList())
                );
            }

        }
    }

    @Override
    @Transactional
    public void onVerify(Long id, VerificationResponse verificationResponse) {
        Optional<Demand> demandOp  = demandRepository.findById(id);
        if(demandOp.isPresent()){
            Demand demand = demandOp.get();
            demand.setNextVerifierId(verificationResponse.getVerifier().getId());
        }
    }

    @Override
    @Transactional
    public void onApprove(Long id, VerificationResponse verificationResponse) {
        Optional<Demand> demandOp  = demandRepository.findById(id);
        if(demandOp.isPresent()){
            Demand demand = demandOp.get();
            demand.setNextApproverId(verificationResponse.getVerifier().getId());
        }
    }

    @Override
    @Transactional
    public void approveComplete(Long id) {
        Optional<Demand> demandOp  = demandRepository.findById(id);
        if(demandOp.isPresent()){
            Demand demand = demandOp.get();
            demand.setStatus(DemandStatus.PENDING);
            demand.setDemandDetails(
                    demand.getDemandDetails().stream().map(demandDetail -> {
                        demandDetail.setStatus(DemandStatus.PENDING);
                        return demandDetail;
                    }).collect(Collectors.toList())
            );
        }
    }

    @Override
    @Transactional
    public void sendForReview(Long id, ReferenceObjectDto reviewer, String comment) {
        Optional<Demand> demandOp  = demandRepository.findById(id);
        if(demandOp.isPresent()){
            Demand demand = demandOp.get();
            demand.setReviewerId(reviewer.getId());
            demand.setStatus(DemandStatus.REVIEW);
        }
    }

    @Override
    public String getNextDemandNo() {
        Optional<Long> demandOptional = demandRepository.findMaxOrderById();
        if(demandOptional.isPresent()){
            Long demandNo = demandOptional.get();
            Long newDemandNo = demandNo + 1;
            return String.format("%05d",newDemandNo);
        }
        return String.format("%05d",1);
    }
}
