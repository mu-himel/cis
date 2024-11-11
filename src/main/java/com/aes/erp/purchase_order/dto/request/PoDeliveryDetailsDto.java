package com.aes.erp.purchase_order.dto.request;

import com.aes.erp.common.ReferenceObjectDto;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PoDeliveryDetailsDto {
    private ReferenceObjectDto warehouse;
    private BigDecimal itemQty;
    private BigDecimal deliveryCharge;
}
