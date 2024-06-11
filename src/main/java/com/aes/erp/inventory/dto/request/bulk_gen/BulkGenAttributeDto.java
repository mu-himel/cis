package com.aes.erp.inventory.dto.request.bulk_gen;

import java.util.List;

import lombok.Data;

@Data
public class BulkGenAttributeDto {
    private String attributeType;
    private String attributeUnit;
    private List<String> attributeValue;

    public String getAttributeValue(){
        return String.join(",", attributeValue);
    }
}
