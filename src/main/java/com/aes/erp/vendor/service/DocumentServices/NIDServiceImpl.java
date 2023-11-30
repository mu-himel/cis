package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.NidResponseDto;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import com.aes.erp.vendor.repository.NIDRepository;
import org.springframework.stereotype.Service;

@Service
public class NIDServiceImpl implements NIDService{
    private final NIDRepository nidRepository;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;

    public NIDServiceImpl(NIDRepository nidRepository, DocumentHolderRepository documentHolderRepository, DocumentService documentService) {
        this.nidRepository = nidRepository;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
    }

    @Override
    public NIDDocument create(NIDDocument nidDocument) {
        return nidRepository.save(nidDocument);
    }

    @Override
    public void update(Long documentHolderId, NidResponseDto dto) {
        NIDDocument nidDocument = nidRepository.getNidDocumentByDocumentHolderId(documentHolderId);
        nidDocument = dto.dtoToEntityMapping(dto, nidDocument);
//        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
//        nidDocument.setDocumentHolder(documentHolder);
        Document document= documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.NID);
        nidDocument.setDocument(document);
        nidRepository.save(nidDocument);
    }
}
