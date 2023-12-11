package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.vendor.entity.Vendor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offer_participator")
public class OfferParticipator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private PartyType partyType;
    @OneToOne
    private Vendor vendor;
    @OneToOne
    private Organization organization;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "offer_id")
    private Offer offer;
}
