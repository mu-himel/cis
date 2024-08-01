package com.aes.erp.inventory.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;

import lombok.Data;

@Data
public class MergePendingCategoryDto {
    private String name;
    private String code;
    private ReferenceObjectDto parentCategory;
    private Long mergeCategoryId;
    private List<CategoryAttribute> attributes;
    private List<String> brands;
    private BigDecimal vat;
    private Organization organization;
}
