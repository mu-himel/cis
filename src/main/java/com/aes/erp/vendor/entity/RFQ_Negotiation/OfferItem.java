package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.scm.Entities.TenderDeliveryDetail;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.vendor.entity.Vendor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"offer"})
@Table(name = "offer_items")
public class OfferItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private String productDescription;
    private String specification;
    private Long estimatedDeliveryDays;
    private Long itemQuantity;
    @OneToOne
    private PriceQuotation priceQuotation;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "offer_id")
    private Offer offer;
}
