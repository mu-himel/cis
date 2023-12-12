package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BinResponseDto {
    @JsonProperty("ID")
    private String ID;
    @JsonIgnore()
    private String secret_key;
    @JsonProperty("Address")
    private String address;
    @JsonProperty("BIN Number")
    private String bin;

    @JsonProperty("Effective Date")
    private String effectiveDate;

    @JsonProperty("Issue Date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date issueDate;

    @JsonProperty("Name of Company")
    private String companyName;
    @JsonProperty("Old BIN Number")
    private String oldBin;
    @JsonProperty("TIN")
    private String tin;
    @JsonProperty("Ownership Type")
    private String ownershipType;
//    @JsonProperty("all_info")
//    private List<String> allInformation;

    public BINDocument dtoToEntityMapping(BinResponseDto dto, BINDocument document){
        if(!dto.getAddress().isEmpty())document.setAddress(dto.getAddress());
        if(!dto.getBin().isEmpty())document.setBin(dto.getBin());
        if(!dto.getOldBin().isEmpty())document.setOldBin(dto.getOldBin());
        if(!dto.getTin().isEmpty())document.setTin(dto.getTin());
        if(!dto.getCompanyName().isEmpty())document.setCompanyName(dto.getCompanyName());
        if(dto.getEffectiveDate() != null)document.setEffectiveDate(dto.getEffectiveDate());
        if(dto.getIssueDate() != null)document.setIssueDate(dto.getIssueDate());
        if(!dto.getOwnershipType().isEmpty())document.setOwnershipType(dto.getOwnershipType());
        return document;
    }
}
