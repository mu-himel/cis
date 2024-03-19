package com.aes.erp.scm.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Data
public class TenderCreateDto {
    private String code;
    private String rfqNo;
    private Long deadline;
    private String itemCategoryCode;
    private List<TenderItemCreateDto> tenderItems;

    public void setDeadline(String deadline){
        ZoneId zoneId = ZoneId.systemDefault();
        this.deadline = LocalDateTime.parse(deadline).atZone(zoneId).toEpochSecond()*1000;
    }
    
}
