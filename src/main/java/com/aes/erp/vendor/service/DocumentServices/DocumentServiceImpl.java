package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.repository.DocumentRepository;
import org.springframework.stereotype.Service;

@Service
public class DocumentServiceImpl implements DocumentService{
    private final DocumentRepository documentRepository;

    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public Document create(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document getDocumentByDocumentHolderIdAndType(Long documentHolderId, DocumentType docType) {
       return documentRepository.getDocumentByDocumentHolderId(documentHolderId, docType.ordinal());
    }
}
