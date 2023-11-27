package com.aes.erp.verification.service;

import com.aes.erp.authentication.JwtUtil;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.authentication.dto.EmployeeInfoDto;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.exception.AesException;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessVerifierConfig;
import com.aes.erp.module_access.repository.ModuleAccessPermissionRepository;
import com.aes.erp.module_access.service.ModuleAccessPermissionService;
import com.aes.erp.module_access.service.ModuleAccessService;
import com.aes.erp.module_access.service.ModuleAccessVerifierConfigService;
import com.aes.erp.verification.dto.request.ApproveDto;
import com.aes.erp.verification.dto.request.CommentDto;
import com.aes.erp.verification.dto.request.VerifyDto;
import com.aes.erp.verification.dto.response.Verifier;
import com.aes.erp.verification.entity.Comment;
import com.aes.erp.verification.entity.Verification;
import com.aes.erp.verification.enums.DomainType;
import com.aes.erp.verification.repository.VerificationRepository;
import com.aes.erp.verification.repository.VerificationRepository.VerificationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class VerificationServiceImpl implements VerificationService {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private ModuleAccessPermissionService moduleAccessPermissionService;

    @Autowired
    private ModuleAccessService moduleAccessService;

    @Autowired
    private ModuleAccessVerifierConfigService moduleAccessVerifierConfigService;

    @Autowired
    private JwtUtil jwtUtil;

    private VerificationDomainService verificationDomainService;

    @Autowired
    private CommentService commentService;

    public void setVerificationDomainService(VerificationDomainService verificationDomainService){
        this.verificationDomainService = verificationDomainService;
    }


    @Override
    public Optional<?> getVerifiers(String token, String uri, Long categoryId, Long subCategoryId) {

        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Map<String,Object> data = new HashMap<>();
//        Optional<ModuleAccessPermissionRepository.PermittedModuleWithVerifier> modulePermissionByUri =
//                (Optional<ModuleAccessPermissionRepository.PermittedModuleWithVerifier>)
//                        moduleAccessPermissionService.getModulePermissionByUri(token, uri);

        Optional<ModuleAccess> moduleAccessOp = moduleAccessService.getModuleAccessByUri(uri);

        if(moduleAccessOp.isPresent()){
//            ModuleAccessPermissionRepository.PermittedModuleWithVerifier modulePermission = modulePermissionByUri.get();
            ModuleAccess module = moduleAccessOp.get();

            List<ModuleAccessVerifierConfig> moduleAccessVerifierConfigs = moduleAccessVerifierConfigService.getVerifierConfigByModule(module);
            Optional<ModuleAccessVerifierConfig> verifierConfigOp = moduleAccessVerifierConfigs.stream().filter(
                    mavc -> {
                       Long criteriaIdValue= Long.parseLong(mavc.getCriteriaIdValue());
                       if(subCategoryId!=null){
                           return criteriaIdValue.equals(subCategoryId) && mavc.getCriteriaGroup()
                                   .equalsIgnoreCase("CATEGORY");
                       }
                       return criteriaIdValue.equals(categoryId) && mavc.getCriteriaGroup()
                                   .equalsIgnoreCase("CATEGORY");

                    }
            ).findFirst();

            if (claimResponseDto.getEmployee() != null) {
                if(verifierConfigOp.isPresent()) {
                    ModuleAccessVerifierConfig verifierConfig = verifierConfigOp.get();
                    EmployeeInfoDto employee = claimResponseDto.getEmployee();
                    if (employee.getReportingManagerId() == null && employee
                            .getLevel().equals(verifierConfig.getLevel())) {
                        // manager self

                        data = prepareVerifierResponse(employee,false, new ArrayList<>());

                    }else if(employee.getReportingManagerId() == null &&
                            employee.getLevel()>verifierConfig.getLevel()) {


                        data = prepareVerifierResponse(employee,true, getReportingManagers(employee,
                                employee.getLevel(),verifierConfig.getLevel()));

                    }else if(employee.getReportingManagerId() !=null &&
                            employee.getLevel()>=verifierConfig.getLevel()
                    ){
                        // need to get verifier list to verification config level
                        // get reporting Manager Profile
                        data = prepareVerifierResponse(employee,true, getReportingManagers(employee,
                                employee.getLevel(),verifierConfig.getLevel()));

                    }
                    return Optional.ofNullable(data);
                }
            }


            return Optional.empty();
        }

        return Optional.empty();
    }

    private List<Verifier> getReportingManagers(EmployeeInfoDto employee, Integer fromLevel, Integer toLevel) {
        return verificationRepository.getVerifierPanel(employee,fromLevel,toLevel);
    }

    private Map<String,Object> prepareVerifierResponse(EmployeeInfoDto employee,
                                                       Boolean verificationStatus,
                                                       List<?> verifers){
        Map<String,Object> data = new HashMap<>();
        data.put("verificationRequired",verificationStatus);
        data.put("employeeDepartment", employee.getDepartmentId());
        data.put("employeeDepartmentLevel", employee.getLevel());
        data.put("parentDepartment",employee.getParentDepartmentId());
        data.put("reportingManager", employee.getReportingManagerId());
        data.put("verifiers",verifers);
        return data;
    }

    @Override
    public void addVerification(Verification verification) {
        verificationRepository.save(verification);
    }

    @Override
    public void addVerification(List<Verification> verifications) {
        verificationRepository.saveAll(verifications);
    }

    @Override
    @Transactional
    public void verify(VerifyDto verifyDto) {
        Employee verifier = new Employee(verifyDto.getVerifier().getId());
        DomainType domainType = verifyDto.getDomainType();
        Long domainId = verifyDto.getDomainId();
        String msg = verifyDto.getComment();

        List<VerificationRepository.VerificationResponse> count = verificationRepository
                    .findAllByDomainTypeAndDomainIdAndVerifiedAndIsApproval(
                            domainType, domainId,false,false);


        Optional<Verification> verificationOp = verificationRepository
                .findByDomainTypeAndDomainIdAndVerifierAndIsApproval(domainType,domainId,verifier,false);
        if(verificationOp.isPresent()){
            Verification verification = verificationOp.get();
            verification.setVerified(true);
            verification.setVerificationDate(LocalDateTime.now());
            verificationRepository.save(verification);
            if(count!=null && count.size()>1 && verificationDomainService!=null){
                if(count.get(1)!=null) {
                    verificationDomainService.onVerify(domainId, count.get(1));
                }
            }

            if(count!=null && count.size()==1 && verificationDomainService!=null){
                List<VerificationResponse> approvalCount = verificationRepository
                        .findAllByDomainTypeAndDomainIdAndVerifiedAndIsApproval(
                                domainType, domainId,false,true);
                Optional<VerificationResponse> firstApprover = Optional.empty();
                if(approvalCount.size()>0){
                    firstApprover = approvalCount.stream().findFirst();
                }
                verificationDomainService.verifyComplete(domainId,firstApprover);
            }

            comment(verifier, domainType, domainId, msg);
        }
    }

    @Override
    @Transactional
    public void approve(ApproveDto verifyDto) {
        Employee verifier = new Employee(verifyDto.getVerifier().getId());
        DomainType domainType = verifyDto.getDomainType();
        Long domainId = verifyDto.getDomainId();
        String msg = verifyDto.getComment();

        List<VerificationRepository.VerificationResponse> count = verificationRepository
                .findAllByDomainTypeAndDomainIdAndVerifiedAndIsApproval(
                        domainType, domainId,false,true);

        Optional<Verification> verificationOp = verificationRepository
                .findByDomainTypeAndDomainIdAndVerifierAndIsApproval(domainType,domainId,verifier,true);

        if(verificationOp.isPresent()) {
            Verification verification = verificationOp.get();
            verification.setVerified(true);
            verification.setVerificationDate(LocalDateTime.now());

            if(count!=null && count.size()>1 && verificationDomainService!=null){
                if(count.get(1)!=null) {
                    verificationDomainService.onApprove(domainId, count.get(1));
                }
            }
            if(count!=null && count.size()==1 && verificationDomainService!=null){
                verificationDomainService.approveComplete(domainId);
            }

            comment(verifier, domainType, domainId, msg);
        }
    }

    private void comment(Employee verifier, DomainType domainType, Long domainId, String msg) {
        if(msg !=null && !msg.isEmpty()){
//            Comment comment = new Comment();
//            comment.setCommentedBy(verifier);
//            comment.setDomainType(domainType);
//            comment.setDomainId(domainId);
//            comment.setMessage(msg);
            Comment comment = commentService.prepareComment(verifier,domainType,domainId,msg);
            commentService.addComment(comment);
        }
    }

    @Override
    public List<VerificationResponse> getVerificationsByDomainTypeAndDomainId(DomainType domainType, Long domainId) {
        return verificationRepository.findAllByDomainTypeAndDomainId(domainType,domainId);
    }

    @Override
    public void review(VerifyDto verifyDto) {

        if(verificationDomainService!=null){
            if(verifyDto.getComment()==null || verifyDto.getComment().isEmpty()){
                throw new AesException("Message Required");
            }
            verificationDomainService.sendForReview(verifyDto.getDomainId(),verifyDto.getReviewer(),verifyDto.getComment());

            comment(new Employee(verifyDto.getVerifier().getId()),
                    verifyDto.getDomainType(),verifyDto.getDomainId(),
                    verifyDto.getComment());

        }
    }
}
