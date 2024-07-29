package com.aes.erp.inventory.dto.request;

import com.aes.erp.inventory.enums.CategoryStatus;

import lombok.Data;

@Data
public class MergePendingCategoryPostDto {
    private String code;
    private CategoryStatus approveStatus;
}
