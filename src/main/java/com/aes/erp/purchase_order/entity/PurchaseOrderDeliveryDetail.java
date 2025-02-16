package com.aes.erp.purchase_order.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@Table(name="purchase_order_delivery_details")
public class PurchaseOrderDeliveryDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Long warehouseId;
    private String warehouseName;
    private String warehouseAddress;

    @Column(precision = 38,scale = 4)
    private BigDecimal itemQty;

    @Column(precision = 38,scale = 4)
    private BigDecimal deliveryCharge;

    @ManyToOne
    @JsonIgnore
    private PurchaseOrderDetail purchaseOrderDetail;
}
