package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.scm.Entities.Tender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"negotiationHistory", "tender", "offerParticipators"})
@Table(name = "offers")
public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private boolean deliveryChargeIncluded;
    private BigDecimal deliveryChargeAmount;

    @Enumerated(value = EnumType.STRING)
    private OfferStage offerStage;

    @Enumerated(value = EnumType.STRING)
    private CreditType creditType;

    private Boolean mushakIncluded;
    private Boolean vatIncluded;
    private BigDecimal vatPercent;
    private BigDecimal vatAmount;
    private Boolean aitIncluded;
    private String note;
    private BigDecimal finalOfferPrice;
    private Long creditPaymentDays;

    private Boolean isFinal;

    private String declineMessage;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<OfferItem> offerItems = new ArrayList<>();

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<OfferTermsAndCondition> termsAndConditions;

    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<OfferDeliveryDetail> warehouses = new ArrayList<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tender_id")
    private Tender tender;

    // @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "negotiation_history_id")
    private NegotiationHistory negotiationHistory;

    @JsonIgnore
    @OneToMany(mappedBy = "offer", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<OfferNegotiator> offerParticipators = new HashSet<>();;

    public void addParticipator(OfferNegotiator participator) {
        if (participator != null) {
            participator.setOffer(this);
            offerParticipators.add(participator);
        }
    }

    public void removeParticipator(OfferNegotiator participator) {
        participator.setOffer(null);
        this.offerParticipators.remove(participator);
    }
}
