package com.aes.erp.scm.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeliveryDetailsCreateDto {
    private Long warehouseId;
    private String wareHouseName;
    private String wareHouseAddress;
    private BigDecimal deliveryOrderQTY;
}
