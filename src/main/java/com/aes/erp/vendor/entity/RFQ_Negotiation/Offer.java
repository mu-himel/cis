package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.scm.Entities.Tender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private boolean deliveryChargeIncluded;
    private Long deliveryChargeAmount;

    @Enumerated(value = EnumType.STRING)
    private OfferStage offerStage;

    @Enumerated(value = EnumType.STRING)
    private CreditType creditType;

    private boolean mushakIncluded;
    private boolean vatIncluded;
    private String note;
    private Long finalOfferPrice;
    private Long creditPaymentDays;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<OfferItem> offerItems;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tender_id")
    private Tender tender;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "negotiation_history_id")
    private NegotiationHistory negotiationHistory;
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferParticipator> participators = new ArrayList<>();

    public void addParticipator(OfferParticipator participator) {
        participators.add(participator);
        participator.setOffer(this);
    }

    public void removeParticipator(OfferParticipator participator) {
        participators.remove(participator);
        participator.setOffer(null);
    }
}
