package com.aes.erp.inventory.dto.request;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.PendingAttribute;

import lombok.Data;

@Data
public class PendingAttributeDto implements EntityConvertable<PendingAttribute>{
    
    private ReferenceObjectDto subCategory;

    private String attributeType;

    private String attributeValue;

    private String attributeUnit;

    @Override
    public PendingAttribute getEntity() {
        PendingAttribute pendingAttribute = new PendingAttribute();
        pendingAttribute.setSubCategory(new ItemCategory(subCategory.getId()));
        pendingAttribute.setAttributeType(this.attributeType);
        pendingAttribute.setAttributeValue(this.attributeValue);
        pendingAttribute.setAttributeUnit(this.attributeUnit);
        return pendingAttribute;
    }

    
}
