package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.TINDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TinResponseDto {
    @JsonProperty("ID")
    @JsonIgnore
    private String ID;
    @JsonIgnore
    private String secret_key;
    @JsonProperty("Full Name")
    private String name;
    @JsonProperty("Father Name")
    private String fatherName;
    @JsonProperty("Mother Name")
    private String motherName;
    @JsonProperty("Current Address")
    private String currentAddress;
    @JsonProperty("Permanent Address")
    private String permanentAddress;
    @JsonProperty("Previous TIN")
    private String previousTIN;
    @JsonProperty("TIN Number")
    private String tin;
    @JsonProperty("Status")
    private String status;
    @JsonProperty("Error")
    private String error;
    public TINDocument dtoToEntityMapping(TinResponseDto dto, TINDocument tinDocument){
        if(!dto.getName().isEmpty())tinDocument.setName(dto.getName());
        if(!dto.getTin().isEmpty())tinDocument.setTin(dto.getTin());
        if(!dto.getFatherName().isEmpty())tinDocument.setFatherName(dto.getFatherName());
        if(!dto.getMotherName().isEmpty())tinDocument.setMotherName(dto.getMotherName());
        if(!dto.getCurrentAddress().isEmpty())tinDocument.setCurrentAddress(dto.getCurrentAddress());
        if(!dto.getPermanentAddress().isEmpty())tinDocument.setPermanentAddress(dto.getPermanentAddress());
        if(!dto.getPreviousTIN().isEmpty())tinDocument.setPreviousTIN(dto.getPreviousTIN());
        return tinDocument;
    }
}
