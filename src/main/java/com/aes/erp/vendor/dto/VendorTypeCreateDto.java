package com.aes.erp.vendor.dto;

import lombok.Data;

@Data
public class VendorTypeCreateDto {
    private String name;

    public VendorTypeCreateDto(String name) {
        this.name = name;
    }
}
