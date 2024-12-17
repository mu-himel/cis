//package com.aes.erp.indent.entity;
//
//import com.aes.erp.indent.enums.IndentPriority;
//import com.aes.erp.indent.enums.PrIndentStatus;
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
//@Table(name = "pr_indents")
//public class PrIndent {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @CreationTimestamp
//    private LocalDateTime prIndentDate;
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
//    private LocalDateTime priorityDate;
//
//    @OneToMany(mappedBy = "prIndent", cascade = CascadeType.PERSIST)
//    private List<PrIndentDetail> prIndentDetails;
//
//    @Enumerated(EnumType.STRING)
//    private PrIndentStatus status;
//
//    public PrIndent(Long id) {
//        this.id = id;
//    }
//}
