package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;
import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class CertificateOfIncorporationDto {
    private String irc_number;
    private String name;
    private String etin;
    private String address;
    @JsonProperty("message")
    private String message;
    @JsonProperty("state")
    private String error;


    public CertificateOfIncorporation dtoToEntityMapping(CertificateOfIncorporationDto dto, CertificateOfIncorporation entity){
        if(!dto.getIrc_number().isEmpty())entity.setCertificateOfIncorporationNo(dto.getIrc_number());
        if(!dto.getName().isEmpty())entity.setCompanyName(dto.getName());

        return entity;
    }
}
