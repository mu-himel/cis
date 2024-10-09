package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.ArticleOfAssociationDto;
import com.aes.erp.vendor.entity.DocmentEntities.ArticleOfAssociation;
import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.repository.ArticleOfAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleOfAssociationServiceImpl implements ArticleOfAssociationService{
    @Autowired
    private ArticleOfAssociationRepository articleOfAssociationRepository;
    @Autowired
    private DocumentService documentService;

    @Override
    public ArticleOfAssociation create(ArticleOfAssociation articleOfAssociation) {
        return articleOfAssociationRepository.save(articleOfAssociation);
    }

    @Override
    public void update(Long documentHolderId, ArticleOfAssociationDto dto) {
        ArticleOfAssociation entity = articleOfAssociationRepository.getCOIDocumentByDocumentHolderId(documentHolderId);
        entity = dto.dtoToEntityMapping(dto, entity);
        Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.AOA);
        entity.setDocument(document);
        articleOfAssociationRepository.save(entity);
    }
}
