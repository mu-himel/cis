package com.aes.erp.inventory.dto.request.bulk_gen;

import java.util.List;

import lombok.Data;

@Data
public class SearchAttributeDto {
    private String attributeType;
    private String attributeUnit;
    private List<String> attributeValue;
}
