//package com.aes.erp.indent.service;
//
//import com.aes.erp.common.ReferenceObjectDto;
//import com.aes.erp.employee.entity.Employee;
//import com.aes.erp.exception.AesException;
//import com.aes.erp.indent.dto.request.IndentRequestDto;
//import com.aes.erp.indent.dto.request.MoveIndentRequestDto;
//import com.aes.erp.indent.entity.Indent;
//import com.aes.erp.indent.entity.IndentDetail;
//import com.aes.erp.indent.enums.IndentPriority;
//import com.aes.erp.indent.enums.IndentStatus;
//import com.aes.erp.indent.enums.IndentVerificationStatus;
//import com.aes.erp.indent.repository.IndentRepository;
//import com.aes.erp.indent.repository.PrIndentRepository;
//import com.aes.erp.inventory.entity.Item;
//import com.aes.erp.inventory.entity.ItemCategory;
//import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
//import com.aes.erp.productrequirment.service.ProductRequirementService;
//import com.aes.erp.verification.dto.response.Verifier;
//import com.aes.erp.verification.entity.Verification;
//import com.aes.erp.verification.enums.DomainType;
//import com.aes.erp.verification.repository.VerificationRepository;
//import com.aes.erp.verification.service.VerificationService;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class IndentServiceImpl implements IndentService {
//    private final ProductRequirementService productRequirementService;
//    private final VerificationService verificationService;
//    private final IndentRepository indentRepository;
//    private final PrIndentRepository prIndentRepository;
//
//    public IndentServiceImpl(
//            ProductRequirementService productRequirementService,
//            IndentRepository indentRepository,
//            VerificationService verificationService,
//            PrIndentRepository prIndentRepository) {
//        this.indentRepository = indentRepository;
//        this.productRequirementService = productRequirementService;
//        this.verificationService = verificationService;
//        this.prIndentRepository = prIndentRepository;
//    }
//
//    @Override
//    public String getNextIndentNo() {
//        Optional<Long> demandOptional = indentRepository.findMaxIndentById();
//        if (demandOptional.isPresent()) {
//            Long demandNo = demandOptional.get();
//            Long newDemandNo = demandNo + 1L;
//            return String.format("%06d", newDemandNo);
//        }
//        return String.format("%06d", 1);
//    }
//
//    @Override
//    public void createIndent(String token, String uri, IndentRequestDto indentRequestDto) {
//        Indent indent = indentRequestDto.getEntity();
//        List<PrIndentRepository.PrIndentViewInfo> prIndents = prIndentRepository.getPrIndentByIds(indentRequestDto.getIds());
//        List<IndentDetail> indentDetails = new ArrayList<>();
//
//        if (CollectionUtils.isNotEmpty(prIndents)) {
//
//            boolean isFirst = true;
//            for (PrIndentRepository.PrIndentViewInfo prIndent : prIndents) {
//                if (isFirst) {
//                    indent.setIndentNo(getNextIndentNo());
//                    indent.setCategory(new ItemCategory(prIndent.getCategoryId()));
//                    indent.setSubCategory(new ItemCategory(prIndent.getSubCategoryId()));
//                    indent.setPriority(IndentPriority.valueOf(prIndent.getPriority()));
//                    indent.setDeliveryLocation(indentRequestDto.getDeliveryLocation());
//                    isFirst = false;
//                }
//                IndentDetail indentDetail = new IndentDetail();
//                indentDetail.setPrQty(prIndent.getOrderQty());
//                indentDetail.setOrderQty(prIndent.getOrderQty());
//                indentDetail.setItem(new Item(prIndent.getItemId()));
//                indentDetail.setIndent(indent);
//
//                indentDetails.add(indentDetail);
//            }
//            indent.setIndentDetails((indentDetails));
//        }
//
//        //        Verification START
//        Long categoryId = (indent.getSubCategory() != null) ? indent.getSubCategory().getId() :
//                indent.getCategory().getId();
//
//        Optional<Map<String, Object>> verifierOp = (Optional<Map<String, Object>>) verificationService
//                .getVerifiers(token, uri, categoryId, indent.getSubCategory().getId());
//
//        List<Verifier> verifiers = new ArrayList<>();
//        if (verifierOp.isPresent()) {
//            Map<String, Object> verification = verifierOp.get();
//            verifiers = (List<Verifier>) verification.get("verifiers");
//            Boolean verificationRequired = (Boolean) verification.get("verificationRequired");
//            if (verificationRequired != null && verificationRequired == true && verifiers != null && verifiers.size() > 0) {
//                indent.setStatus(IndentVerificationStatus.PENDING_VERIFICATION);
//            } else {
//                indent.setStatus(IndentVerificationStatus.PENDING);
//            }
//
//        } else {
//            indent.setStatus(IndentVerificationStatus.PENDING);
//        }
////        Verification END
//
//        indent.setIndentNo(getNextIndentNo());
//        indent.setIstatus(IndentStatus.INIT);
//
//        //Save Indent
//        indent = indentRepository.save(indent);
//
//        //Update Product Requirement Status
//        productRequirementService.updateStatusByCategoryAndSubCategory(ProductRequirmentStatus.CLOSE,
//                ProductRequirmentStatus.OPEN,
//                indent.getCategory().getId(),
//                indent.getSubCategory().getId());
//        setVerifiers(indent, verifiers);
//    }
//
//    private void setVerifiers(Indent indent, List<Verifier> verifiers) {
//        if (verifiers.size() > 0) {
//            Optional<Verifier> firstOp = verifiers.stream().findFirst();
//            Verifier _verifier = firstOp.get();
//            List<Verification> verifications = verifiers.stream().map(verifier -> {
//                Verification verification = new Verification();
//                verification.setDomainId(indent.getId());
//                verification.setDomainType(DomainType.INDENT);
//                verification.setVerified(false);
//                verification.setIsApproval(false);
//                verification.setVerifier(new Employee(verifier.getId()));
//                return verification;
//            }).collect(Collectors.toList());
//            indent.setNextVerifierId(_verifier.getId());
//            verificationService.addVerification(verifications);
//        }
//    }
//
//    @Override
//    public Page<?> getAllIndents(Optional<Integer> page,
//                                 Optional<Integer> size,
//                                 Optional<Long> categoryId,
//                                 Optional<Long> subCategoryId,
//                                 Optional<String> priority) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "id");
//        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
//        Page<?> result = indentRepository.getAllIndents(
//                categoryId.orElse(null),
//                subCategoryId.orElse(null),
//                priority.orElse(null),
//                pageable);
//        return result;
//    }
//
//    @Override
//    public List<?> getIndentById(Optional<Long> indentId) {
//        List<?> result = indentRepository.getIndentById(
//                indentId.orElseThrow(() -> new AesException("Indent id should not empty"))
//        );
//        return result;
//    }
//
//    @Override
//    public List<?> getIndentByIds(Optional<List<Long>> indentIds) {
//        List<?> result = indentRepository.getIndentByIds(
//                indentIds.orElseThrow(() -> new AesException("Indent ids should not empty"))
//        );
//        return result;
//    }
//
//    @Transactional
//    @Override
//    public int moveIndentByIds(MoveIndentRequestDto moveIndent) {
//        if (CollectionUtils.isEmpty(moveIndent.getIds())) {
//            throw new AesException("Indents should not empty");
//        }
//        int result = indentRepository.moveIndentByIds(moveIndent.getIds());
//        return result;
//    }
//
//
//    @Override
//    public void onVerify(Long id, VerificationRepository.VerificationResponse verificationResponse) {
//
//    }
//
//    @Override
//    public void onApprove(Long id, VerificationRepository.VerificationResponse verificationResponse) {
//
//    }
//
//    @Override
//    public void verifyComplete(Long id, Optional<VerificationRepository.VerificationResponse> firstApprover) {
//
//    }
//
//    @Override
//    public void approveComplete(Long id) {
//
//    }
//
//    @Override
//    public void sendForReview(Long id, ReferenceObjectDto reviewer, String comment) {
//
//    }
//}
