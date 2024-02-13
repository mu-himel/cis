package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.RFQ_Negotiation.CreditType;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OfferCreateDTO {
    private List<OfferItem> offerItems;
    private CreditType creditType;
    private boolean mushakIncluded;
    private BigDecimal deliveryChargeAmount;
    private boolean deliveryChargeIncluded;
    private boolean vatIncluded;
    private String note;
    private BigDecimal finalOfferPrice;
    private Long creditPaymentDays;
    //Only used for counter offer
    private Long negotiationHistoryId;
}
