package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.TradeDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeLicenseDto {
    @JsonProperty("ID")
    @JsonIgnore
    private String ID;
    @JsonIgnore
    private String secret_key;
    @JsonProperty("Issue Date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date issueDate;
    @JsonProperty("Mobile Number")
    private String mobileNo;
    @JsonProperty("NID Number")
    private String nid;
    private String license;
    @JsonProperty("None")
    @JsonIgnore
    private String ignore;

    public TradeDocument dtoToEntityMapping(TradeLicenseDto dto, TradeDocument tradeDocument){
        if(dto.getIssueDate() != null)tradeDocument.setIssueDate(dto.getIssueDate());
        if(!dto.getNid().isEmpty())tradeDocument.setNid(dto.getNid());
        if(!dto.getMobileNo().isEmpty())tradeDocument.setMobileNo(dto.getMobileNo());
        if(!dto.getLicense().isEmpty())tradeDocument.setTradeLicenseNumber(dto.getLicense());
        return tradeDocument;
    }
}
