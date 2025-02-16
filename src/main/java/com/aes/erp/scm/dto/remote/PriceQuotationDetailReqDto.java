package com.aes.erp.scm.dto.remote;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PriceQuotationDetailReqDto {
    private String itemAttributeName;
    private String extendedAttributes;
    private Integer warrantyDuration;
    private String warrantyUnit;
    private String brandName;
    private BigDecimal rfqQty;
    private BigDecimal unitPrice;
    private Integer estDeliveryDays;
    private List<PriceQuotationDeliveryDetailDto> deliveryDetails;
}
