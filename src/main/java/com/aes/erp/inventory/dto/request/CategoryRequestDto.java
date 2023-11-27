package com.aes.erp.inventory.dto.request;

import com.aes.erp.common.EntityConvertible;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.CategoryBudget;
import com.aes.erp.inventory.entity.ItemCategory;
import io.swagger.annotations.ApiModel;
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
public class CategoryRequestDto implements EntityConvertible<ItemCategory> {

    private Long id;

    @NotBlank(message = "Name is required")
    @ApiModelProperty(required = true)
    private String name;

    @ApiModelProperty(required = true)
    private String code;

    private BigDecimal currentYearBudget;
    private Optional<Long> budgetId;

    private ItemCategory parentCategory;

    private List<CategoryAttribute> attributes;

    private BigDecimal vat;



    @Override
    @ApiModelProperty(hidden = true)
    public ItemCategory getEntity() {
        ItemCategory category = new ItemCategory(id);
        BeanUtils.copyProperties(this,category);
        return category;
    }
}
