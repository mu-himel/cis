package com.aes.erp.scm.dto.remote;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ItemDeliveryDetailDto {
    private String itemName;
    private BigDecimal deliveryOrderQty;
}
