package com.aes.erp.scm.dto.remote;

import java.math.BigDecimal;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.ItemCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class GrnManualItemDetailDto {
    private ReferenceObjectDto item;
    private ItemCategory category;
    private ItemCategory subCategory;
    private Integer estDeliveryDays;
    private BigDecimal orderQty;
    private BigDecimal pricePerUnit;
}
