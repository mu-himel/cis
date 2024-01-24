package com.aes.erp.scm.DtoCollection;

import lombok.Data;

@Data
public class DeliveryDetailsCreateDto {
    private String wareHouseName;
    private String wareHouseAddress;
    private Long deliveryOrderQTY;
}
