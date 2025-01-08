package com.aes.erp.scm.dto.remote;

import java.math.BigDecimal;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.ItemCategory;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class GrnManualItemDetailDto {
    private String itemCode;
    private ReferenceObjectDto category;
    private ReferenceObjectDto subCategory;
    private Long estDeliveryDays;
    private BigDecimal orderQty;
    private BigDecimal pricePerUnit;
    private BigDecimal deliveryChargeAmount;
    private BigDecimal vatAmount;

}
