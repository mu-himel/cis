package com.aes.erp.scm.dto.remote;

import java.math.BigDecimal;
import java.util.List;

public class DeliveryDetailDto {
    private Long warehouseId;
    private String deliveryChargeMode;
    private BigDecimal deliveryChargeAmount;
    private List<ItemDeliveryDetailDto> items;
}
