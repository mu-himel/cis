package com.aes.erp.inventory.dto.request;

import java.util.List;

import com.aes.erp.inventory.entity.ItemAttribute;
import com.aes.erp.inventory.entity.ItemCategory;

import lombok.Data;

@Data
public class ItemMergeRequestDto{
    private Long mergeItemId;
    private String name;
    private String code;
    private ItemCategory itemCategory;
    private ItemCategory itemParentCategory;
    private String itemUnit;
    private List<ItemAttribute> attributes;
    String itemAttributeName;
    String brand;
}
