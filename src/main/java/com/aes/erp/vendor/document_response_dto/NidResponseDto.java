package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NidResponseDto {
    // @JsonFormat(pattern = "dd MMM yyyy")
    private String dateOfBirth;
    private String name;
    private String nidNumber;
    private String address;
    private String fatherName;
    private String motherName;
    private String banglaName;
    @JsonProperty("Error")
    private String error;

    public NIDDocument dtoToEntityMapping(NidResponseDto dto, NIDDocument nidDocument){
        if(!dto.getNidNumber().isEmpty())nidDocument.setNid(dto.getNidNumber());
        if(!dto.getName().isEmpty())nidDocument.setName(dto.getName());
        if(!dto.getBanglaName().isEmpty())nidDocument.setName(dto.getBanglaName());
        if(!dto.getFatherName().isEmpty())nidDocument.setFatherName(dto.getFatherName());
        if(!dto.getMotherName().isEmpty())nidDocument.setMotherName(dto.getMotherName());
        if(dto.getDateOfBirth() != null)nidDocument.setDateOfBirth(getTimestamp(dto.getDateOfBirth(), "dd MMM yyyy"));
        return nidDocument;
    }
    
    private Timestamp getTimestamp(String date,String format){
		DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDate ld = LocalDate.parse(date,df);
        return Timestamp.valueOf(ld.atStartOfDay());
    }
}
