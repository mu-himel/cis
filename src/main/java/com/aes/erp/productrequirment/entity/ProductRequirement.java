//package com.aes.erp.productrequirment.entity;
//
//import com.aes.erp.demand.entity.Demand;
//import com.aes.erp.demand.entity.DemandDetail;
//import com.aes.erp.inventory.entity.ItemCategory;
//import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//
//import javax.persistence.*;
//import java.time.LocalDateTime;
//
////@Entity
//@Getter
//@Setter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "product_requirements")
//public class ProductRequirement {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @CreationTimestamp
//    private LocalDateTime productRequirementDate;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ItemCategory category;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private ItemCategory subCategory;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private Demand demand;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    private DemandDetail demandDetail;
//
//    @Enumerated(EnumType.STRING)
//    private ProductRequirmentStatus status;
//
//    public ProductRequirement(Long id) {
//        this.id = id;
//    }
//}
