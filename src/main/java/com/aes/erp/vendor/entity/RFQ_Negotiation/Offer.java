package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.scm.Entities.Tender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
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
    private CreditType creditType;
    private boolean mushakIncluded;
    private boolean vatIncluded;
    private String note;
    private Long finalOfferPrice;
    private Long creditPaymentDays;
    @OneToMany(mappedBy = "offer", cascade = CascadeType.ALL)
    private List<OfferItem> offerItems;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tender_id")
    private Tender tender;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negotiation_history_id")
    private NegotiationHistory negotiationHistory;
}
