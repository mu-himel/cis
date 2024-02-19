package com.aes.erp.scm.dto;

import lombok.Data;

import java.util.List;

@Data
public class TenderItemCreateDto {
    private String productDescription;
    private String specification;
    private Long orderQuantity;
    private List<DeliveryDetailsCreateDto> deliveryDetails;
}
