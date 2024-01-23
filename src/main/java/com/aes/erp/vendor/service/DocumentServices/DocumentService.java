package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;

public interface DocumentService {
    Document create(Document document);
    Document getDocumentByDocumentHolderIdAndType(Long documentHolderId, DocumentType documentType);
}
