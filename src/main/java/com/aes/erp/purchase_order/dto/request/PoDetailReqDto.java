package com.aes.erp.purchase_order.dto.request;

import java.math.BigDecimal;
import java.util.List;

import com.aes.erp.common.ReferenceObjectDto;

import lombok.Data;

@Data
public class PoDetailReqDto {
//    private ReferenceObjectDto warehouse;
    private String itemName;
    private BigDecimal itemQty;
    private BigDecimal deliveryCharge;
    private BigDecimal vatAmount;
    private BigDecimal vatPercent;
    private BigDecimal subTotal;
    private BigDecimal totalPrice;
    private List<PoDeliveryDetailsDto> poDeliveryDetailsDtoList;
}
