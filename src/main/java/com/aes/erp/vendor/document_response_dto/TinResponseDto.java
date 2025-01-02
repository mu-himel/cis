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
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "father_name")
    private String fatherName;
    @JsonProperty(value = "mother_name")
    private String motherName;
    @JsonProperty(value = "current_address")
    private String currentAddress;
    @JsonProperty(value = "permanent_address")
    private String permanentAddress;
    @JsonProperty(value = "previous_tin")
    private String previousTin;
    @JsonProperty(value = "tin")
    private String tinNumber;
    @JsonProperty(value = "date")
    private String date;
    @JsonProperty(value = "status")
    private String tinStatus;
    @JsonProperty("state")
    private String error;
    @JsonProperty("message")
    private String message;

    public TINDocument dtoToEntityMapping(TinResponseDto dto, TINDocument tinDocument) {
        if (!dto.getName().isEmpty()) tinDocument.setName(dto.getName());
        if (!dto.getTinNumber().isEmpty()) tinDocument.setTin(dto.getTinNumber());
        if (!dto.getFatherName().isEmpty()) tinDocument.setFatherName(dto.getFatherName());
        if (!dto.getMotherName().isEmpty()) tinDocument.setMotherName(dto.getMotherName());
        if (!dto.getCurrentAddress().isEmpty()) tinDocument.setCurrentAddress(dto.getCurrentAddress());
        if (!dto.getPermanentAddress().isEmpty()) tinDocument.setPermanentAddress(dto.getPermanentAddress());
        if (!dto.getPreviousTin().isEmpty()) tinDocument.setPreviousTIN(dto.getPreviousTin());
        return tinDocument;
    }
}
