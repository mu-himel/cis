package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.MemorandumOfAssociationDto;
import com.aes.erp.vendor.entity.DocmentEntities.ArticleOfAssociation;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocmentEntities.MemorandumOfAssociation;
import com.aes.erp.vendor.repository.MemorandumOfAssociationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemorandumOfAssociationServiceImpl implements MemorandumOfAssociationService{
    @Autowired
    private MemorandumOfAssociationRepository memorandumOfAssociationRepository;
    @Autowired
    private DocumentService documentService;

    @Override
    public MemorandumOfAssociation create(MemorandumOfAssociation memorandumOfAssociation) {
        return memorandumOfAssociationRepository.save(memorandumOfAssociation);
    }

    @Override
    public void update(Long documentHolderId, MemorandumOfAssociationDto dto) {
        MemorandumOfAssociation entity = memorandumOfAssociationRepository.getCOIDocumentByDocumentHolderId(documentHolderId);
        entity = dto.dtoToEntityMapping(dto, entity);
        Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.MOA);
        entity.setDocument(document);
        memorandumOfAssociationRepository.save(entity);
    }
}
