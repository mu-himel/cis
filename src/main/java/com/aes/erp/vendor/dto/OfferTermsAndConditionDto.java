package com.aes.erp.vendor.dto;

import lombok.Data;

import javax.validation.constraints.Max;

@Data
public class OfferTermsAndConditionDto {
    // private Long offerId;
    // private Long vendorId;
    // private Long tenderId;
    @Max(value = 500)
    private String termsAndCondition;
}
