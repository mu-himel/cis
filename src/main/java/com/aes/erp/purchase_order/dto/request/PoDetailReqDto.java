package com.aes.erp.purchase_order.dto.request;

import java.math.BigDecimal;

import com.aes.erp.common.ReferenceObjectDto;

import lombok.Data;

@Data
public class PoDetailReqDto {
    private String itemName;
    private BigDecimal itemQty;
}
