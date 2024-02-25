package com.aes.erp.scm.dto.remote;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceQuotationDeliveryDetailDto {
    private String warehouseName;
    private String deliveryChargeType;
    private BigDecimal deliveryOrderQty;
    private BigDecimal deliveryChargeAmount;
}
