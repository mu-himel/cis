package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offer_negotiator")
public class OfferNegotiator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private OfferPartyType partyType;

    @ManyToOne
    @JoinColumn(name = "negotiator_id")
    private Negotiator negotiator;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;

}
