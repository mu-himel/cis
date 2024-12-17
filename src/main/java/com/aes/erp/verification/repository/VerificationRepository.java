//package com.aes.erp.verification.repository;
//
//import com.aes.erp.employee.entity.Employee;
//import com.aes.erp.organogram_system.repository.DepartmentRepository;
//import com.aes.erp.organogram_system.repository.RoleNodeRepository;
//import com.aes.erp.verification.entity.Verification;
//import com.aes.erp.verification.enums.DomainType;
//import com.aes.erp.verification.service.VerifierPanelService;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Repository
//public interface VerificationRepository extends JpaRepository<Verification,Long>, VerifierPanelService {
//    Optional<Verification> findByDomainTypeAndDomainIdAndVerifierAndIsApproval(
//            DomainType domainType, Long domainId, Employee verifier,Boolean isApproval);
//
//
//    @Query(value = "SELECT v FROM Verification v " +
//            "LEFT JOIN FETCH v.verifier ve " +
//            "LEFT JOIN FETCH ve.department d " +
//            "LEFT JOIN FETCH ve.roleNode rn " +
//            "WHERE v.domainType=:domainType AND v.domainId=:domainId")
//    List<VerificationResponse> findAllByDomainTypeAndDomainId(
//            @Param("domainType") DomainType domainType,
//            @Param("domainId") Long domainId);
//
//    @Query(value = "SELECT v FROM Verification v " +
//            "LEFT JOIN FETCH v.verifier ve " +
//            "LEFT JOIN FETCH ve.department d " +
//            "LEFT JOIN FETCH ve.roleNode rn " +
//            "WHERE v.domainType=:domainType AND v.domainId=:domainId " +
//            "AND v.verified=:verified AND v.isApproval=:isApproval")
//    List<VerificationResponse> findAllByDomainTypeAndDomainIdAndVerifiedAndIsApproval(
//            @Param("domainType") DomainType domainType,
//            @Param("domainId") Long domainId,
//            @Param("verified") Boolean verified,
//            @Param("isApproval") Boolean isApproval);
//
//    interface VerificationResponse{
//        Long getId();
//        DomainType getDomainType();
//        Long getDomainId();
//        EmployeeInfo getVerifier();
//
//        Boolean getVerified();
//        Boolean getIsApproval();
//        LocalDateTime getVerificationDate();
//
//    }
//
//
//
//    interface EmployeeInfo{
//        Long getId();
//        String getEmployeeId();
//
//        String getName();
//        DepartmentRepository.DepartmentInfo getDepartment();
//        RoleNodeRepository.RoleNodeInfo getRoleNode();
//
//    }
//}
//
