package com.aes.erp.inventory.dto.request;

import lombok.Data;

@Data
public class MergePendingItemsDto {
    private String name;
    private String code;
    private Long mergeItemId;
}
