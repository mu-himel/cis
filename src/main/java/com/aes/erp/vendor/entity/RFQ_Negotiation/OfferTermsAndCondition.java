package com.aes.erp.vendor.entity.RFQ_Negotiation;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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

    private String termsAndCondition;
}
