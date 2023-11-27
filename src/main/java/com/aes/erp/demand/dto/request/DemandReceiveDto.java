package com.aes.erp.demand.dto.request;

import lombok.Data;

@Data
public class DemandReceiveDto {
    private Long demandId;
    private Integer qty;
    private Long demandDetailId;
    private String note;
}
