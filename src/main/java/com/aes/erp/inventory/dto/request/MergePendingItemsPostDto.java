package com.aes.erp.inventory.dto.request;

import lombok.Data;

@Data
public class MergePendingItemsPostDto {
    private String code;
    private String approveStatus;
    private Long warehouseId;
    private Long warehouseStoreId;
    private Boolean isMerged;
    ItemMergeRequestDto itemMergeRequestDto;
}
