package com.aes.erp.vendor.entity.RFQ_Negotiation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "offer_delivery_details")
public class OfferDeliveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long warehouseId;

    private String deliveryChargeMode;

    private BigDecimal deliveryChargeAmount;

    @OneToMany(mappedBy = "offerDeliveryDetail",cascade = CascadeType.ALL)
    List<OfferItemDeliveryDetail> items;

    @ManyToOne
    private Offer offer;
}
