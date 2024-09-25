package com.aes.erp.scm.dto.remote;

import java.math.BigDecimal;
import java.util.List;

import com.aes.erp.common.ReferenceObjectDto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class GoodReceivedManualRequestDto {
    private String indentNo;
    private ReferenceObjDto category;
    private VendorRemoteDto vendor;
    private String grnNo;
    private String deliveryCharge;
    private String mushak;
    private BigDecimal deliveryChargeAmount;
    private Integer days;
    private String vatOption;
    private String aitOption;
    private BigDecimal totalPrice;
    private BigDecimal totalVat;
//    private BigDecimal vatPctg;
    private BigDecimal inTotal;
    private Long warehouseId;
    private String payment;
    private String invoicePath;
    List<GrnManualItemDetailDto> grnDetails;

}
