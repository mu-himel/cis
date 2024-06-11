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
    
    @JsonIgnore
    private String id;
    @JsonIgnore
    private String secretKey;
    private String name;
    private String fatherName;
    private String motherName;
    private String currentAddress;
    private String permanentAddress;
    private String previousTin;
    private String tinNumber;
    private String tinStatus;
    @JsonProperty("Error")
    private String error;
    
    public TINDocument dtoToEntityMapping(TinResponseDto dto, TINDocument tinDocument){
        if(!dto.getName().isEmpty())tinDocument.setName(dto.getName());
        if(!dto.getTinNumber().isEmpty())tinDocument.setTin(dto.getTinNumber());
        if(!dto.getFatherName().isEmpty())tinDocument.setFatherName(dto.getFatherName());
        if(!dto.getMotherName().isEmpty())tinDocument.setMotherName(dto.getMotherName());
        if(!dto.getCurrentAddress().isEmpty())tinDocument.setCurrentAddress(dto.getCurrentAddress());
        if(!dto.getPermanentAddress().isEmpty())tinDocument.setPermanentAddress(dto.getPermanentAddress());
        if(!dto.getPreviousTin().isEmpty())tinDocument.setPreviousTIN(dto.getPreviousTin());
        return tinDocument;
    }
}
