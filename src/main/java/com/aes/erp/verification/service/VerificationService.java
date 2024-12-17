//package com.aes.erp.verification.service;
//
//import com.aes.erp.employee.entity.Employee;
//import com.aes.erp.verification.dto.request.ApproveDto;
//import com.aes.erp.verification.dto.request.VerifyDto;
//import com.aes.erp.verification.entity.Verification;
//import com.aes.erp.verification.enums.DomainType;
//import com.aes.erp.verification.repository.VerificationRepository;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface VerificationService {
//
//
//
//    Optional<?> getVerifiers(String token, String uri, Long categoryId, Long subCategoryId);
//
//    void addVerification(Verification verification);
//    void addVerification(List<Verification> verifications);
//
//    void setVerificationDomainService(VerificationDomainService verificationDomainService);
//
//    void verify(VerifyDto verifyDto);
//    void approve(ApproveDto verifyDto);
//
//    List<VerificationRepository.VerificationResponse> getVerificationsByDomainTypeAndDomainId(DomainType domainType, Long domainId);
//
//    void review(VerifyDto verifyDto);
//}
