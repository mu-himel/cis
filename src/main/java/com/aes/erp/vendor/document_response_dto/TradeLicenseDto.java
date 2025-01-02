package com.aes.erp.vendor.document_response_dto;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.aes.erp.vendor.entity.DocmentEntities.TradeDocument;
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
    @JsonProperty("issue_date")
    private String issueDate;
    @JsonProperty("issue_time")
    private String issueTime;
    private String mobileNo;
    private String nid;
    @JsonProperty("license_no")
    private String tradeLicenseNo;

    //    @JsonProperty(value = "nameOfBusiness")
    @JsonProperty(value = "business_name")
    private String businessInstituteName;
    @JsonProperty(value = "business_start_date")
    private String businessStartDate;
    @JsonProperty("owner_name")
    private String ownerName;
    @JsonProperty("father_name")
    private String fatherOrHusbandName;
    @JsonProperty("mother_name")
    private String motherName;
    //    @JsonProperty(value = "natureOfBusiness")
    @JsonProperty("business_nature")
    private String businessNature;
//    @JsonProperty(value = "typeOfBusiness")
    @JsonProperty(value = "business_type")
    private String businessType;
//    @JsonProperty(value = "businessAddress")
    @JsonProperty(value = "business_address")
    private String instituteAddress;
    private String instituteArea;
    @JsonProperty("nid_or_passport")
    private String nidPassportNo;
    private String phone;
    @JsonProperty(value = "fiscal_year")
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
    private String ownerIdentification;
    private String validity;
    @JsonProperty("bin_no")
    private String bin;
    private String email;

    //New added field tracked on 26th June, 2024
    private String cityCorporationName;



    @JsonProperty("None")
    @JsonIgnore
    private String ignore;

    @JsonProperty("Error")
    private String error;

    public TradeDocument dtoToEntityMapping(TradeLicenseDto dto, TradeDocument tradeDocument){
        if(dto.getIssueDate() != null && !(dto.getIssueDate().equals("null") || dto.getIssueDate().equals("")))
        {
            tradeDocument.setIssueDate(getTimestamp(dto.getIssueDate(), "dd MMM yyyy"));
        }
        if(dto.getNidPassportNo()!=null && !dto.getNidPassportNo().isEmpty())tradeDocument.setNid(dto.getNidPassportNo());
        // if(!dto.getMobileNo().isEmpty())tradeDocument.setMobileNo(dto.getMobileNo());
        if(!dto.getPhone().isEmpty())tradeDocument.setMobileNo(dto.getPhone());
        if(!dto.getTradeLicenseNo().isEmpty())tradeDocument.setTradeLicenseNumber(dto.getTradeLicenseNo());
        return tradeDocument;
    }

    private Timestamp getTimestamp(String date,String format){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDate ld = LocalDate.parse(date,df);
        return Timestamp.valueOf(ld.atStartOfDay());
    }
}
