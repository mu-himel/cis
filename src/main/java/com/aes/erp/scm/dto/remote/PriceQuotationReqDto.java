package com.aes.erp.scm.dto.remote;

import lombok.Data;

import java.util.List;

@Data
public class PriceQuotationReqDto {
    private String code;
    private String paymentMethod;
    private Long vendorId;
    private String vendorName;
    private List<PriceQuotationDetailReqDto> details;
    private PriceQuotationSummaryDto priceQuotationSummary;
}
