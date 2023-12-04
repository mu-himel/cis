package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.RFQ_Negotiation.CreditType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OfferCreateDTO {
    private List<OfferItemCreateDto> offerItems = new ArrayList<>();
    private CreditType creditType;
    private boolean mushakIncluded;
    private Long deliveryChargeAmount;
    private boolean deliveryChargeIncluded;
    private boolean vatIncluded;
    private String note;
    private Long finalOfferPrice;
    private Long creditPaymentDays;
}
