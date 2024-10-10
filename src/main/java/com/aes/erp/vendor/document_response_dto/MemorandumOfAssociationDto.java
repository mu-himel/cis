package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;
import com.aes.erp.vendor.entity.DocmentEntities.MemorandumOfAssociation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class MemorandumOfAssociationDto {
    private String companyName;


    public MemorandumOfAssociation dtoToEntityMapping(MemorandumOfAssociationDto dto, MemorandumOfAssociation entity){
        if(!dto.getCompanyName().isEmpty())entity.setCompanyName(dto.getCompanyName());

        return entity;
    }
}
