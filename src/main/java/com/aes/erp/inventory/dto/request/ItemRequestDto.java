package com.aes.erp.inventory.dto.request;


import com.aes.erp.common.EntityConvertable;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.enums.ItemUnit;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto implements EntityConvertable<Item> {

    private Long id;

    @NotBlank(message = "name is required")
    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(required = true)
    private String code;

    private String sku;

    private ItemCategory itemCategory;
    private ItemCategory itemParentCategory;

    private ItemUnit itemUnit;

    private Integer stockThresholdQty;

    private Integer currentStockQty;

    private Integer reorderPercentage;

    private ReferenceObjectDto brand;

    private List<ItemAttribute> attributes;

    @Override
    @ApiModelProperty(hidden = true)
    public Item getEntity() {
        Item item = new Item(id);
        BeanUtils.copyProperties(this,item);
        return item;
    }
}
