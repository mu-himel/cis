package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.BinResponseDto;
import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.repository.BINRepository;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import org.springframework.stereotype.Service;

@Service
public class BinServiceImpl implements BinService{
    private final BINRepository binRepository;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;

    public BinServiceImpl(BINRepository binRepository, DocumentHolderRepository documentHolderRepository, DocumentService documentService) {
        this.binRepository = binRepository;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
    }

    @Override
    public BINDocument create(BINDocument binDocument) {
       return binRepository.save(binDocument);
    }

    @Override
    public void update(Long documentHolderId, BinResponseDto dto) {
        BINDocument binDocument = binRepository.getBinDocumentByDocumentHolderId(documentHolderId);
        binDocument = dto.dtoToEntityMapping(dto, binDocument);
//        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
        Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.BIN);
        binDocument.setDocument(document);
//        binDocument.setDocumentHolder(documentHolder);
        binRepository.save(binDocument);
    }
}
