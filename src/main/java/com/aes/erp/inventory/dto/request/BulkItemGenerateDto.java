package com.aes.erp.inventory.dto.request;

import java.util.Optional;

import lombok.Data;

@Data
public class BulkItemGenerateDto {
    private Long categoryId;
    private Long subCategoryId;
}
