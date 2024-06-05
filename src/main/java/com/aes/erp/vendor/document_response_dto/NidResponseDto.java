package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NidResponseDto {
    // @JsonFormat(pattern = "dd MMM yyyy")
    private String dateOfBirth;
    private String eName;
    private String nid;

    public NIDDocument dtoToEntityMapping(NidResponseDto dto, NIDDocument nidDocument){
        if(!dto.getNid().isEmpty())nidDocument.setNid(dto.getNid());
        if(!dto.getEName().isEmpty())nidDocument.setEName(dto.getEName());
        if(dto.getDateOfBirth() != null)nidDocument.setDateOfBirth(new Timestamp(Instant.parse(dto.getDateOfBirth()).getEpochSecond()));
        return nidDocument;
    }
}
