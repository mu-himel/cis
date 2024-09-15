package com.aes.erp.scm.dto.remote;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PriceQuotationSummaryDto {
    private String deliveryCharge;
    private BigDecimal deliveryChargeAmount;
    private Boolean mushak;
    private Boolean isVatAdded;
    private String vatPercent;
    private String vatAmount;
    private Boolean isAitAdded;
    private BigDecimal subTotalPrice;
    private BigDecimal totalPrice;
    private Long creditPaymentDuration;
    private String creditPaymentUnit;
    private String note;

    public String getDeliveryCharge(){
        return this.deliveryCharge.toUpperCase();
    }

    public String setDeliveryCharge(String deliveryCharge){
        return this.deliveryCharge = deliveryCharge.toUpperCase();
    }
}
