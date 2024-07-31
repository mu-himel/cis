package com.aes.erp.inventory.dto.request;

import java.util.List;

import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.CategoryAttribute;

import lombok.Data;

@Data
public class MergePendingCategoryDto {
    private String name;
    private String code;
    private Long mergeCategoryId;
    private List<CategoryAttribute> attributes;
    private List<Brand> brands;
}
