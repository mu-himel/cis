package com.aes.erp.scm.dto.remote;

import java.util.List;

public class PriceQuotationDReqDto {
    private Long rfqId;
    private String paymentMethod;
    private Long vendorId;
    private String vendorName;
    private List<PriceQuotationDetailReqDto> details;
}
