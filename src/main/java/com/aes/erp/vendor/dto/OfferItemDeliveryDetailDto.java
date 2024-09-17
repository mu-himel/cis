package com.aes.erp.vendor.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OfferItemDeliveryDetailDto {
    private String itemName;
    private BigDecimal deliveryOrderQty;
}