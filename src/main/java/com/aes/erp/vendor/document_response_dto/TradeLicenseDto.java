package com.aes.erp.vendor.document_response_dto;

import java.sql.Timestamp;
import java.util.Date;

import com.aes.erp.vendor.entity.DocmentEntities.TradeDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TradeLicenseDto {
    @JsonProperty("ID")
    @JsonIgnore
    private String ID;
    @JsonIgnore
    private String secret_key;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date issueDate;
    private String issueTime;
    private String mobileNo;
    private String nid;
    private String tradeLicenseNo;

    private String businessInstituteName;
    private String businessStartDate;
    private String ownerName;
    private String fatherOrHusbandName;
    private String motherName;
    private String businessNature;
    private String businessType;
    private String instituteAddress;
    private String instituteArea;
    private String nidPassportNo;
    private String phone;
    private String fiscalYear;
    private String ownerPresentAddress;
    private String presentAddressHolding;
    private String presentAddressRoad;
    private String presentAddressVillage;
    private String presentAddressPostCode;
    private String presentAddressPS;
    private String presentAddressDistrict;
    private String presentAddressDivision;
    private String ownerPermanentAddress;
    private String permanentAddressHolding;
    private String permanentAddressRoad;
    private String permanentAddressVillage;
    private String permanentAddressPostCode;
    private String permanentAddressPS;
    private String permanentAddressDistrict;
    private String permanentAddressDivision;
    private String licenseRenewFee;
    private String due;
    private String formFee;
    private String correctionFee;
    private String total;
    private String bookPrice;
    private String signboardVat;
    private String vat;
    private String others;
    private String licenseExpireDate;





    
    @JsonProperty("None")
    @JsonIgnore
    private String ignore;
    @JsonProperty("Error")
    private String error;

    public TradeDocument dtoToEntityMapping(TradeLicenseDto dto, TradeDocument tradeDocument){
        if(dto.getIssueDate() != null)tradeDocument.setIssueDate(new Timestamp(dto.getIssueDate().getTime()));
        if(!dto.getNidPassportNo().isEmpty())tradeDocument.setNid(dto.getNidPassportNo());
        // if(!dto.getMobileNo().isEmpty())tradeDocument.setMobileNo(dto.getMobileNo());
        if(!dto.getPhone().isEmpty())tradeDocument.setMobileNo(dto.getPhone());
        if(!dto.getTradeLicenseNo().isEmpty())tradeDocument.setTradeLicenseNumber(dto.getTradeLicenseNo());
        return tradeDocument;
    }
}
