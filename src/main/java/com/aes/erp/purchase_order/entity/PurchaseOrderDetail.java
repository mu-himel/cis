package com.aes.erp.purchase_order.entity;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "purchase_order_details")
public class PurchaseOrderDetail {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal itemQty;

    @ManyToOne
    private OfferItem offerItem;

    private String itemName;
    

    @ManyToOne
    @JsonIgnore
    private PurchaseOrder purchaseOrder;
}
