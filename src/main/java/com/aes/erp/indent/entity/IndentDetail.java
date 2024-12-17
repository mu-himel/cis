//package com.aes.erp.indent.entity;
//
//import com.aes.erp.inventory.entity.Item;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@Table(name = "indent_details")
//public class IndentDetail {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    @OneToOne(fetch = FetchType.LAZY)
//    private Item item;
//
//    private Long prQty;
//
//    private Long orderQty;
//
//    @ManyToOne
//    @ApiModelProperty(hidden = true)
//    private Indent indent;
//
//}
