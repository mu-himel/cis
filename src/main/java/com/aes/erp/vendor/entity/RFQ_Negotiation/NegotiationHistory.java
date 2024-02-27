package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.scm.Entities.Tender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"tender", "offers"})
@Table(name = "negotiation_history")
public class NegotiationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @JsonIgnore
    @OneToMany(mappedBy = "negotiationHistory", cascade = CascadeType.ALL)
    private List<Offer> offers;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tender_id")
    private Tender tender;

    @JsonIgnore
    @OneToMany(mappedBy = "negotiationHistory", cascade = CascadeType.ALL)
    private Set<Negotiator> negotiators = new HashSet<>();

    public void addNegotiators(Negotiator negotiator) {
        if (negotiator != null) {
            negotiator.setNegotiationHistory(this);
            negotiators.add(negotiator);
        }
    }

    public void removeParticipator(Negotiator negotiator) {
        negotiator.setNegotiationHistory(null);
        this.negotiators.remove(negotiator);
    }
}

