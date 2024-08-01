package com.aes.erp.inventory.dto.request;

import java.util.List;

import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.ItemAttribute;
import com.aes.erp.inventory.entity.ItemCategory;

import lombok.Data;

@Data
public class MergePendingItemsDto {
    private String name;
    private String code;
    private Long mergeItemId;
    private String itemUnit;

    private List<ItemAttribute> attributes;
    private ItemCategory itemCategory;
    private ItemCategory itemParentCategory;
    private Brand brand;


}
