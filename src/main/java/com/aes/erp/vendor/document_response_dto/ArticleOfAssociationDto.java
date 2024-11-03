package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.ArticleOfAssociation;
import com.aes.erp.vendor.entity.DocmentEntities.MemorandumOfAssociation;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ArticleOfAssociationDto {

    private String address;
    @JsonProperty("company_name")
    private String companyName;
    @JsonProperty("year_of_establishment")
    private String yearOfEstablishment;
    @JsonProperty("state")
    private String error;
    @JsonProperty("message")
    private String message;

    public ArticleOfAssociation dtoToEntityMapping(ArticleOfAssociationDto dto, ArticleOfAssociation entity) {
        if (!dto.getCompanyName().isEmpty()) entity.setCompanyName(dto.getCompanyName());
        if (!dto.getYearOfEstablishment().isEmpty()) entity.setYearOfEstablishment(dto.getYearOfEstablishment());
        if (!dto.getAddress().isEmpty()) entity.setAddress(dto.getAddress());
        return entity;
    }
}
