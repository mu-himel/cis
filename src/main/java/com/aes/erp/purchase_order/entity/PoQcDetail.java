package com.aes.erp.purchase_order.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "po_qc_details")
public class PoQcDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private PurchaseOrder purchaseOrder;

    private Long poId;
    private String date;
    private String itemAttributeName;
    private String brandName;
    private BigDecimal totalApprovedQty;
    private BigDecimal totalDeclinedQty;
}
