package com.aes.erp.productrequirment.dto.request;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.demand.entity.Demand;
import com.aes.erp.demand.entity.DemandDetail;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.productrequirment.entity.ProductRequirement;
import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequirementRequestDto implements EntityConvertable<ProductRequirement> {

    private Long id;

    @NotNull(message = "Item Main Category Missing")
    private ItemCategory category;
    @NotNull(message = "Item Sub Category Missing")
    private ItemCategory subCategory;

    @NotNull(message = "Demand Missing")
    private Demand demand;

    @NotNull(message = "Demand Details Missing")
    private DemandDetail demandDetail;

    public ProductRequirementRequestDto(Long id) {
        this.id = id;
    }

    @Override
    @ApiModelProperty(hidden = true)
    public ProductRequirement getEntity() {
        ProductRequirement productRequirement = new ProductRequirement(id);
        BeanUtils.copyProperties(this, productRequirement);
        return productRequirement;
    }
}
