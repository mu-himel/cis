package com.aes.erp.inventory.dto.request;

import lombok.Data;

@Data
public class ImportCategoryScmIdUpdateDto {
    private Long categoryIdCps;
    private Long categoryIdScm;
}
