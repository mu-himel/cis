package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinResponseDto {
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String secretKey;
    private String address;
    private String binNumber;
    private String effectiveDate;

    private String issueDate;
    private String companyName;
    private String oldBinNumber;
    private String tinNumber;
    private String ownershipType;

    public BINDocument dtoToEntityMapping(BinResponseDto dto, BINDocument document){
        if(!dto.getAddress().isEmpty())document.setAddress(dto.getAddress());
        if(!dto.getBinNumber().isEmpty())document.setBin(dto.getBinNumber());
        if(!dto.getOldBinNumber().isEmpty())document.setOldBin(dto.getOldBinNumber());
        if(!dto.getTinNumber().isEmpty())document.setTin(dto.getTinNumber());
        if(!dto.getCompanyName().isEmpty())document.setCompanyName(dto.getCompanyName());
        if(dto.getEffectiveDate() != null)document.setEffectiveDate(dto.getEffectiveDate());
        if(dto.getIssueDate() != null)document.setIssueDate(new Timestamp(Instant.parse(dto.getIssueDate()).getEpochSecond()));
        if(!dto.getOwnershipType().isEmpty())document.setOwnershipType(dto.getOwnershipType());
        return document;
    }
}
