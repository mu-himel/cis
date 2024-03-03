package com.aes.erp.purchase_order.dto.request;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class PoRequestDto {
    private String poNo;
    private Long vendorId;
    private String tenderNo;
    private Long poDate;
    private String categoryCode;
    private Long deliveryDate;
    private Long offerId;

    private List<PoDetailReqDto> orderDetails;

}
