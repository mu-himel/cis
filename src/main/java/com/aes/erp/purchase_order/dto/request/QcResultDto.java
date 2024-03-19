package com.aes.erp.purchase_order.dto.request;

import lombok.Data;

@Data
public class QcResultDto {
    String note;
    String qcResult;
    String status;
}
