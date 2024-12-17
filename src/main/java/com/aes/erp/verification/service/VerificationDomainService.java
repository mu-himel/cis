//package com.aes.erp.verification.service;
//
//import com.aes.erp.common.ReferenceObjectDto;
//import com.aes.erp.verification.repository.VerificationRepository;
//import com.aes.erp.verification.repository.VerificationRepository.VerificationResponse;
//
//import java.util.Optional;
//
//public interface VerificationDomainService {
//    void onVerify(Long id, VerificationResponse verificationResponse);
//
//    void onApprove(Long id, VerificationResponse verificationResponse);
//    void verifyComplete(Long id, Optional<VerificationResponse> firstApprover);
//    void approveComplete(Long id);
//
//    void sendForReview(Long id,ReferenceObjectDto reviewer, String comment);
//}
