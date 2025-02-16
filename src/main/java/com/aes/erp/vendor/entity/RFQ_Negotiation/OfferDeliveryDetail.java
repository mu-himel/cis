package com.aes.erp.vendor.entity.RFQ_Negotiation;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

    @Column(precision = 38, scale = 4)
    private BigDecimal deliveryChargeAmount;

    @OneToMany(mappedBy = "offerDeliveryDetail", cascade = CascadeType.ALL)
    List<OfferItemDeliveryDetail> items;

    @ManyToOne
    @JsonIgnore
    private Offer offer;
}
