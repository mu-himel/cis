package com.aes.erp.demand.dto.request;

import com.aes.erp.demand.entity.DemandDetailAttribute;
import com.aes.erp.demand.enums.DemandPriority;
import lombok.Data;

import java.util.List;

@Data
public class DemandDetailDto {
    private DemandItemDto item;
    private DemandCategoryDto subCategory;
    private DemandCategoryDto category;
    private Integer requestQuantity;
    private String specification;
    private DemandPriority priority;

    private List<DemandDetailAttribute> attributes;
}
