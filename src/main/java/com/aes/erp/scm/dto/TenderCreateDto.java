package com.aes.erp.scm.dto;

import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TenderCreateDto {
    private String code;
    private String rfqNo;
    private String deadline;
    private String itemCategoryCode;
    private List<TenderItemCreateDto> tenderItems;

    public Long getDeadline(){
        return Instant.parse(deadline).toEpochMilli();
    }
}
