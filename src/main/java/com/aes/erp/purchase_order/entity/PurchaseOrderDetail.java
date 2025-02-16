package com.aes.erp.purchase_order.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;

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

    @Column(precision = 38,scale = 4)
    private BigDecimal itemQty;

    @ManyToOne
    private OfferItem offerItem;

    private String itemName;

//    private Long warehouseId;
    @Column(precision = 38,scale = 4)
    private BigDecimal deliveryCharge;

    @Column(precision = 38,scale = 4)
    private BigDecimal vatAmount;

    @Column(precision = 38,scale = 4)
    private BigDecimal vatPercent;

    @Column(precision = 38,scale = 4)
    private BigDecimal subTotal;

    @Column(precision = 38,scale = 4)
    private BigDecimal totalPrice;
    
    @ManyToOne
    @JsonIgnore
    private PurchaseOrder purchaseOrder;

    @OneToMany(mappedBy = "purchaseOrderDetail",cascade = CascadeType.ALL)
    private List<PurchaseOrderDeliveryDetail> purchaseOrderDeliveryDetails;
}
