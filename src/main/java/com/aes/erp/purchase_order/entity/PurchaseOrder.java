package com.aes.erp.purchase_order.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.vendor.entity.Vendor;
import lombok.Data;

@Data
@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vendor vendor;

    private Long remotePoId;
    private String tenderNo;
    private String poNo;
    private Long poDate;
    private Long deliveryDate;
    private BigDecimal itemQty;
    private String categoryCode;

    private Boolean isPoSent;

    private String poStatus;

    @Column(length = 500)
    private String invoicePath;

    @Column(length = 1000)
    private String qcResult;

    @Column(length = 500)
    private String grnDeclineNote;

    private Boolean isGrnReceived;

    private Long grnReceiveDate;

    private Boolean isQcPass;

//    private Long warehouseId;

    @Column(length = 500)
    private String qcDeclineNote;
    private String deliveryChargeType;

    @ManyToOne
    private Organization org;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
    private List<PurchaseOrderDetail> orderDetails;

    @OneToMany(mappedBy = "purchaseOrder")
    private List<PoQcDetail> qcDetails;


}
