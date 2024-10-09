package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.ArticleOfAssociationDto;
import com.aes.erp.vendor.document_response_dto.CertificateOfIncorporationDto;
import com.aes.erp.vendor.entity.DocmentEntities.ArticleOfAssociation;

public interface ArticleOfAssociationService {
    ArticleOfAssociation create(ArticleOfAssociation articleOfAssociation);
    void update(Long documentHolderId, ArticleOfAssociationDto dto);
}
