package com.aes.erp.scm.dto;

import lombok.Data;

@Data
public class DeliveryDetailsCreateDto {
    private Long warehouseId;
    private String wareHouseName;
    private String wareHouseAddress;
    private Long deliveryOrderQTY;
}
