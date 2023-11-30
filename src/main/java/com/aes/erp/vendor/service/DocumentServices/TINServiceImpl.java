package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.TinResponseDto;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocmentEntities.TINDocument;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import com.aes.erp.vendor.repository.TINRepository;
import org.springframework.stereotype.Service;

@Service
public class TINServiceImpl implements TINService{
    private final TINRepository tinRepository;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;

    public TINServiceImpl(TINRepository tinRepository, DocumentHolderRepository documentHolderRepository, DocumentService documentService) {
        this.tinRepository = tinRepository;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
    }

    @Override
    public TINDocument create(TINDocument tinDocument) {
        return tinRepository.save(tinDocument);
    }

    @Override
    public void update(Long documentHolderId, TinResponseDto dto) {
        TINDocument tinDocument = tinRepository.getTinDocumentByDocumentHolderId(documentHolderId);
        tinDocument = dto.dtoToEntityMapping(dto, tinDocument);
        Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.TIN);
        tinDocument.setDocument(document);
        tinRepository.save(tinDocument);
    }
}
