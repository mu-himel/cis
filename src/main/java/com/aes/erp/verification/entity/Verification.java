//package com.aes.erp.verification.entity;
//
//import com.aes.erp.employee.entity.Employee;
//import com.aes.erp.verification.dto.response.Verifier;
//import com.aes.erp.verification.enums.DomainType;
//import lombok.Data;
//import org.hibernate.annotations.CreationTimestamp;
//
//import javax.persistence.*;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Entity
//@SqlResultSetMapping(name = "Verifier",
//        classes = @ConstructorResult(
//                targetClass = Verifier.class,
//                columns = {
//                        @ColumnResult(name = "id",type = Long.class),
//                        @ColumnResult(name = "name",type = String.class),
//                        @ColumnResult(name = "verified",type = Boolean.class),
//                        @ColumnResult(name = "departmentId",type = Long.class),
//                        @ColumnResult(name = "departmentName",type = String.class),
//                        @ColumnResult(name = "departmentLevel",type = Integer.class),
//                        @ColumnResult(name = "designationId",type = Long.class),
//                        @ColumnResult(name = "designation",type = String.class)
//                }
//        )
//)
//@Data
//@Table(name = "verifications")
//public class Verification {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private Long domainId;
//
//    @Enumerated(EnumType.STRING)
//    private DomainType domainType;
//
//    @ManyToOne
//    private Employee verifier;
//
//    private Boolean verified;
//
//    private Boolean isApproval=false;
//
//
//    private LocalDateTime verificationDate;
//}
