package com.aes.erp.inventory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryAttributeDto {
    private String attributeType;
    private String attributeValue;
    private String attributeUnit;
}
