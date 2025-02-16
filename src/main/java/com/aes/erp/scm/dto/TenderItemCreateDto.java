package com.aes.erp.scm.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TenderItemCreateDto {
    private String productDescription;
    private String brandName;
    private String specification;
    private BigDecimal orderQuantity;
    private List<DeliveryDetailsCreateDto> deliveryDetails;
}
