package com.aes.erp.inventory.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;

import lombok.Data;

@Data
public class MergePendingCategoryDto {
    private String name;
    private String code;
    private ItemCategory parentCategory;
    private Long mergeCategoryId;
    private List<CategoryAttribute> attributes;
    private List<Brand> brands;
    private BigDecimal vat;
}
