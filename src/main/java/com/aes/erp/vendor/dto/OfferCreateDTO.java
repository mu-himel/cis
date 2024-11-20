package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.RFQ_Negotiation.CreditType;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferTermsAndCondition;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class OfferCreateDTO {
    private List<OfferItemCreateDto> offerItems;
    private List<OfferDeliveryDetailDto> warehouses;
    private CreditType creditType;
    private Boolean mushakIncluded;
    private BigDecimal totalDeliveryChargeAmount;
    private Boolean aitIncluded;
    private Boolean vatIncluded;
    private BigDecimal vatAmount;
    private BigDecimal aitAmount;
    private BigDecimal vatPercent;
    private BigDecimal aitPercent;
    private String note;
    private BigDecimal finalOfferPrice;
    private Integer creditPaymentDays;
    private Boolean isFinal;
    private List<OfferTermsAndConditionDto> termsAndConditions = new ArrayList<>();
    //Only used for counter offer
    @ApiModelProperty(value = "Only for counter offer")
    private Long negotiationHistoryId;
}
