package com.aes.erp.scm.dto;

import lombok.Data;

import java.util.List;

@Data
public class TenderCreateDto {
    private String code;
    private String itemCategoryCode;
    private List<TenderItemCreateDto> tenderItems;
}
