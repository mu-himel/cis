package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.TradeLicenseDto;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocmentEntities.TradeDocument;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import com.aes.erp.vendor.repository.TRADERepository;
import org.springframework.stereotype.Service;

@Service
public class TradeLicenseServiceImpl implements TradeLicenseService{
    private final TRADERepository tradeRepository;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;

    public TradeLicenseServiceImpl(TRADERepository tradeRepository, DocumentHolderRepository documentHolderRepository, DocumentService documentService) {
        this.tradeRepository = tradeRepository;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
    }

    @Override
    public TradeDocument create(TradeDocument tradeDocument) {
       return tradeRepository.save(tradeDocument);
    }

    @Override
    public void update(Long documentHolderId, TradeLicenseDto dto) {
        TradeDocument tradeDocument = tradeRepository.getTradeLicenseDocumentByDocumentHolderId(documentHolderId);
        tradeDocument = dto.dtoToEntityMapping(dto, tradeDocument);
//        DocumentHolder documentHolder = documentHolderRepository.getReferenceById(documentHolderId);
//        tradeDocument.setDocumentHolder(documentHolder);
        Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.TRADE);
        tradeDocument.setDocument(document);
        tradeRepository.save(tradeDocument);
    }
}
