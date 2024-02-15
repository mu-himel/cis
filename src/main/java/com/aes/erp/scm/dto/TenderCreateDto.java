package com.aes.erp.scm.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TenderCreateDto {
    private String code;
    private String rfqNo;
    private String deadline;
    private String itemCategoryCode;
    private List<TenderItemCreateDto> tenderItems;

    public LocalDateTime getDeadline(){
        return LocalDateTime.parse(this.deadline);
    }
}
