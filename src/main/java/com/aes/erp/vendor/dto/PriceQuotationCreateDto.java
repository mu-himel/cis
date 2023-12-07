package com.aes.erp.vendor.dto;

import lombok.Data;

@Data
public class PriceQuotationCreateDto {
    private Long pricePerUnit;
    private Long totalPrice;
}
