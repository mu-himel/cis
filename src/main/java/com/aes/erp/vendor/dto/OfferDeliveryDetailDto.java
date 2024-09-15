package com.aes.erp.vendor.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class OfferDeliveryDetailDto {
    private Long warehouseId;
    private String deliveryChargeMode;
    private BigDecimal deliveryChargeAmount;
    private List<OfferItemDeliveryDetailDto> items;

    public String getDeliveryChargeMode(){
        return this.deliveryChargeMode.toUpperCase();
    }

    public String setDeliveryChargeMode(String deliveryChargeMode){
        return this.deliveryChargeMode = deliveryChargeMode.toUpperCase();
    }
}
