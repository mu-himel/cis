package com.aes.erp.scm.dto.remote;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceQuotationDeliveryDetailDto {
    private Long warehouseId;
    private String warehouseName;
    private String deliveryChargeType;
    private BigDecimal deliveryOrderQty;
    private BigDecimal deliveryChargeAmount;

    public String getDeliveryChargeType(){
        return this.deliveryChargeType.toUpperCase();
    }

    public String setDeliveryChargeType(String deliveryChargeType){
        return this.deliveryChargeType = deliveryChargeType.toUpperCase();
    }
}
