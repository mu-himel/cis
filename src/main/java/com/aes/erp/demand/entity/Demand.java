//package com.aes.erp.demand.entity;
//
//import com.aes.erp.demand.enums.DemandStatus;
//import com.aes.erp.employee.entity.Employee;
//import com.aes.erp.inventory.entity.ItemCategory;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
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
//@Table(name = "demands")
//public class Demand {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    private String demandNo;
//
//    private LocalDateTime demandDate;
//
//    @Enumerated(EnumType.STRING)
//    private DemandStatus status;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ItemCategory category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ItemCategory subCategory;
//
//    @ManyToOne
//    private Employee requestedBy;
//
//    private Long reviewerId;
//
//    private Long nextVerifierId;
//    private Long nextApproverId;
//
//    @OneToMany(mappedBy = "demand", cascade = CascadeType.ALL)
//    private List<DemandDetail> demandDetails;
//
//
//
//}
