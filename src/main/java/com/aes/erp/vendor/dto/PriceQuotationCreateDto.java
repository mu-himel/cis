package com.aes.erp.vendor.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PriceQuotationCreateDto {
    private BigDecimal pricePerUnit;
    private BigDecimal totalPrice;
}
