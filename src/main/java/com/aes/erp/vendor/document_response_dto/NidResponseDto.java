package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NidResponseDto {
    @JsonProperty("Date of Birth")
    @JsonFormat(pattern = "dd MMM yyyy")
    private Date dateOfBirth;
    @JsonProperty("Full Name")
    private String eName;
    @JsonProperty("NID Number")
    private String nid;
    @JsonProperty("Error")
    private String error;

    public NIDDocument dtoToEntityMapping(NidResponseDto dto, NIDDocument nidDocument){
        if(!dto.getNid().isEmpty())nidDocument.setNid(dto.getNid());
        if(!dto.getEName().isEmpty())nidDocument.setEName(dto.getEName());
        if(dto.getDateOfBirth() != null)nidDocument.setDateOfBirth(dto.getDateOfBirth());
        return nidDocument;
    }
}
