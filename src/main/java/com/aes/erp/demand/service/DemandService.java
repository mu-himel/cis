//package com.aes.erp.demand.service;
//
//import com.aes.erp.demand.dto.request.DemandReceiveDto;
//import com.aes.erp.demand.dto.request.DemandRequestDto;
//import com.aes.erp.demand.dto.request.ReviewDto;
//import com.aes.erp.verification.service.VerificationDomainService;
//import org.springframework.data.domain.Page;
//
//import java.util.Optional;
//
//public interface DemandService extends VerificationDomainService {
//
//    void createDemand(String token, String uri, DemandRequestDto demandRequestDto);
//
//    Page<?> getMyDemands(Long id, Optional<Integer> page, Optional<Integer> size);
//    Page<?> getAllDemands(String token, Optional<Integer> page, Optional<Integer> size);
//
//    Optional<?> getDemandDetail(Long id);
//
//    void receiveDemandItem(String token, DemandReceiveDto demandReceiveDto);
//    void declineDemandItem(String token, DemandReceiveDto demandReceiveDto);
//
//    void sentDemandItem(DemandReceiveDto demandReceiveDto);
//
//    Page<?> getAllCloseDemands(Optional<Integer> page, Optional<Integer> size);
//
//    String getNextDemandNo();
//
//    Page<?> getAllPendingVerificationDemands(String token, Optional<Integer> page, Optional<Integer> size);
//    Page<?> getAllPendingApprovalDemands(String token, Optional<Integer> page, Optional<Integer> size);
//
//    void reviewDemand(String token, Long id, ReviewDto reviewDto);
//
//    void rejectDemandItem(String token, DemandReceiveDto demandReceiveDto);
//    void resendDemandItem(String token, DemandReceiveDto demandReceiveDto);
//}
