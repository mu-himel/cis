package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.inventory.entity.Organization;
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
@EqualsAndHashCode(exclude = {"negotiationHistory"})
@Table(name = "negotiator")
public class Negotiator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private NegotiationPartyType partyType;
    @OneToOne
    private Vendor vendor;
    @OneToOne
    private Organization organization;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "negotiation_history_id")
    private NegotiationHistory negotiationHistory;
}
