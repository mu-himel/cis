package com.aes.erp.scm.DtoCollection;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.scm.Entities.TenderItem;
import lombok.Data;

import java.util.List;

@Data
public class TenderCreateDto {
    private Long itemCategoryId;
    private Long orgId;
    private List<TenderItem> tenderItems;
}
