//package com.aes.erp.indent.entity;
//
//import com.aes.erp.employee.entity.Employee;
//import com.aes.erp.indent.enums.IndentPriority;
//import com.aes.erp.indent.enums.IndentStatus;
//import com.aes.erp.indent.enums.IndentVerificationStatus;
//import com.aes.erp.inventory.entity.ItemCategory;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Entity
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "indents")
//public class Indent {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String indentNo;
//
//    @CreationTimestamp
//    private LocalDateTime indentDate;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ItemCategory category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ItemCategory subCategory;
//
//    @Enumerated(EnumType.STRING)
//    private IndentPriority priority;
//
//    @OneToMany(mappedBy = "indent", cascade = CascadeType.PERSIST)
//    private List<IndentDetail> indentDetails;
//
//    private String deliveryLocation;
//
//    @Enumerated(EnumType.STRING)
//    private IndentStatus istatus;
//
//    @Enumerated(EnumType.STRING)
//    private IndentVerificationStatus status;
//
//    // Verification
//    @ManyToOne
//    private Employee requestedBy;
//
//    private Long reviewerId;
//
//    private Long nextVerifierId;
//    private Long nextApproverId;
//
//    public Indent(Long id) {
//        this.id = id;
//    }
//}
