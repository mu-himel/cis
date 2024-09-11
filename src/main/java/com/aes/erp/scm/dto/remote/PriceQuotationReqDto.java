package com.aes.erp.scm.dto.remote;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import com.aes.erp.vendor.dto.OfferDeliveryDetailDto;

@Data
public class PriceQuotationReqDto {
    private Long rfqId;
    private String code;
    private String paymentMethod;
    private Long remoteOfferId;
    private Long vendorId;
    private String vendorName;
    private String vendorEmail;
    private String vendorPhoneNo;
    private String vendorType;
    private Integer score;
    private Long negotiationHistoryId;
    private Boolean isFinal;
    private List<PriceQuotationDetailReqDto> details;
    private PriceQuotationSummaryDto priceQuotationSummary;
    private List<String> termsAndConditions = new ArrayList<>();
    private List<OfferDeliveryDetailDto> warehouses;
    private String file;
}
