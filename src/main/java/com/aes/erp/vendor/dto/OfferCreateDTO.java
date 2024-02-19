package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.RFQ_Negotiation.CreditType;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
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
    private String note;
    private BigDecimal finalOfferPrice;
    private Long creditPaymentDays;
    //Only used for counter offer
    @ApiModelProperty(value = "Only for counter offer")
    private Long negotiationHistoryId;
}
