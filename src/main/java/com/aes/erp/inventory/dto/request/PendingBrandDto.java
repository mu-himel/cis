package com.aes.erp.inventory.dto.request;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.PendingBrand;

import lombok.Data;

@Data
public class PendingBrandDto implements EntityConvertable<PendingBrand> {
    private String brandName;
    private ReferenceObjectDto subCategory;

    @Override
    public PendingBrand getEntity() {
        PendingBrand pendingBrand = new PendingBrand();
        pendingBrand.setBrandName(this.brandName);
        pendingBrand.setSubCategory(new ItemCategory(subCategory.getId()));
        return pendingBrand;
    }

    
}
