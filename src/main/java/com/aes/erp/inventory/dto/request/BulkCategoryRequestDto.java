package com.aes.erp.inventory.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class BulkCategoryRequestDto {
    private List<CategoryRequestDto> categories;
    private String userId;
    private Long warehouseId;
    private Long warehouseStoreId;
}
