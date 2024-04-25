package com.aes.erp.purchase_order.dto.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class QcDetailDto {
    private Long poId;
    private String date;
    private String itemAttributeName;
    private String brandName;   
    private BigDecimal totalApprovedQty;
    private BigDecimal totalDeclinedQty;
    
}