package com.aes.erp.vendor.entity.RFQ_Negotiation;

import javax.persistence.*;
import javax.validation.constraints.Max;

import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.vendor.entity.Vendor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "offer_term_and_conditions")
public class OfferTermsAndCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JsonIgnore
    private Vendor vendor;

    @ManyToOne
    @JsonIgnore
    private Offer offer;

    @ManyToOne
    @JsonIgnore
    private Tender tender;

    @Column(length = 500)
    private String termsAndCondition;
}
