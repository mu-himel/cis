package com.aes.erp.inventory.dto.request;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = {"currentYearBudget","budgetId","requestedBy","entity"})
public class CategoryRequestDto implements EntityConvertable<ItemCategory> {

    private Long id;

    @NotBlank(message = "Name is required")
    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(required = true)
    private String code;

    private ItemCategory parentCategory;

    private List<CategoryAttribute> attributes;
    private List<String> brands;
    private StoreType storeType;

    private BigDecimal vat;

    private Organization organization;

    private ReferenceObjectDto warehouse;
    private ReferenceObjectDto warehouseStore;

    private Long cpsCategoryId;

    private Long scmCategoryId;

    @Override
    @ApiModelProperty(hidden = true)
    public ItemCategory getEntity() {
        ItemCategory category = new ItemCategory(id);
        BeanUtils.copyProperties(this,category);
        return category;
    }
}
