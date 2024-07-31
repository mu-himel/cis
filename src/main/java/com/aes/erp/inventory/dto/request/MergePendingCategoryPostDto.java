package com.aes.erp.inventory.dto.request;

import java.util.List;

import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.enums.CategoryStatus;

import lombok.Data;

@Data
public class MergePendingCategoryPostDto {
    private String code;
    private CategoryStatus approveStatus;
    private List<CategoryAttribute> attributes;
    private List<Brand> brands;
}
