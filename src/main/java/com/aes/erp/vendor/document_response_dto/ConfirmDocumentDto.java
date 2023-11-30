package com.aes.erp.vendor.document_response_dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.HashMap;

@Data
public class ConfirmDocumentDto {
    private BankSolvencyDto bankSolvency;
    private BinResponseDto  bin;
    private TinResponseDto tin;
    private TradeLicenseDto trade;
    private NidResponseDto nid;
}
