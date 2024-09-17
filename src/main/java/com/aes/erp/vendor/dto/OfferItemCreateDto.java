package com.aes.erp.vendor.dto;

import lombok.Data;

@Data
public class OfferItemCreateDto {
    private String productDescription;//
    private String extendedAttributes;
    private String brandName;
    private String specification;//
    private String location;
    private Long itemQuantity;//
    private Long estimatedDeliveryDays;//
    private Integer warrantyDuration;//
    private String warrantyUnit;//
    private PriceQuotationCreateDto priceQuotation;//
}
