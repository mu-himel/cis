package com.aes.erp.purchase_order.dto.request;

import java.util.List;

import com.aes.erp.common.ReferenceObjectDto;

import lombok.Data;

@Data
public class PoRequestDto {
    private Long id;
    private String poNo;
    private Long vendorId;
    private String tenderNo;
    private Long poDate;
    private String categoryCode;
    private Long deliveryDate;
    private Long offerId;
    private String deliveryChargeType;


    private List<PoDetailReqDto> orderDetails;

}
