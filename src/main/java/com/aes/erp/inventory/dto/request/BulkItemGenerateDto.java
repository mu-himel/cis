package com.aes.erp.inventory.dto.request;

import java.util.List;
import java.util.Optional;

import com.aes.erp.inventory.entity.CategoryAttribute;

import lombok.Data;

@Data
public class BulkItemGenerateDto {
    private Long categoryId;
    private Long subCategoryId;
    private List<CategoryAttribute> attributes;
}
