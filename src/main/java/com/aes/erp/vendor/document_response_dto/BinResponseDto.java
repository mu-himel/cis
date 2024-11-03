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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    @JsonProperty(value = "address")
    private String address;
    @JsonProperty(value = "bin")
    private String binNumber;
    @JsonProperty(value = "effective_date")
    private String effectiveDate;
    @JsonProperty(value = "etin")
    private String eTin;
    @JsonProperty(value = "issue_date")
    private String issueDate;
    @JsonProperty(value = "name")
    private String companyName;
    @JsonProperty(value = "old_bin")
    private String oldBinNumber;
    private String tinNumber;
    @JsonProperty(value = "ownership")
    private String ownershipType;
    @JsonProperty(value = "trading_brand_name")
    private String tradingBrandName;
    @JsonProperty(value = "major_area_of_economic_activity")
    private String majorAreaofEcoAct;
    @JsonProperty("state")
    private String error;
    @JsonProperty("message")
    private String message;

    public BINDocument dtoToEntityMapping(BinResponseDto dto, BINDocument document) {
        if (!dto.getAddress().isEmpty()) document.setAddress(dto.getAddress());
        if (!dto.getBinNumber().isEmpty()) document.setBin(dto.getBinNumber());
        if (!dto.getOldBinNumber().isEmpty()) document.setOldBin(dto.getOldBinNumber());
        if (!dto.getTinNumber().isEmpty()) document.setTin(dto.getTinNumber());
        if (!dto.getCompanyName().isEmpty()) document.setCompanyName(dto.getCompanyName());
        if (dto.getEffectiveDate() != null) document.setEffectiveDate(dto.getEffectiveDate());
        if (dto.getIssueDate() != null) document.setIssueDate(getTimestamp(dto.getIssueDate(), "dd/MM/yyyy"));
        if (!dto.getOwnershipType().isEmpty()) document.setOwnershipType(dto.getOwnershipType());
        return document;
    }

    private Timestamp getTimestamp(String date,String format){
		DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDate ld = LocalDate.parse(date,df);
        return Timestamp.valueOf(ld.atStartOfDay());
    }
}
