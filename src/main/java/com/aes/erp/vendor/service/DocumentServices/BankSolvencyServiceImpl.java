package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.BankSolvencyDto;
import com.aes.erp.vendor.entity.DocmentEntities.BankSolvencyDocument;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.repository.BankSolvencyRepository;
import com.aes.erp.vendor.repository.DocumentHolderRepository;
import org.springframework.stereotype.Service;

@Service
public class BankSolvencyServiceImpl implements BankSolvencyService{
    private final BankSolvencyRepository bankSolvencyRepository;
    private final DocumentHolderRepository documentHolderRepository;
    private final DocumentService documentService;

    public BankSolvencyServiceImpl(BankSolvencyRepository bankSolvencyRepository, DocumentHolderRepository documentHolderRepository, DocumentService documentService) {
        this.bankSolvencyRepository = bankSolvencyRepository;
        this.documentHolderRepository = documentHolderRepository;
        this.documentService = documentService;
    }

    @Override
    public BankSolvencyDocument create(BankSolvencyDocument bankSolvencyDocument) {
        return bankSolvencyRepository.save(bankSolvencyDocument);
    }

    @Override
    public void update(Long documentHolderId, BankSolvencyDto dto) {
       BankSolvencyDocument bankSolvencyDocument = bankSolvencyRepository.getBankSolvencyDocumentByDocumentHolderId(documentHolderId);
       bankSolvencyDocument = dto.dtoToEntityMapping(dto, bankSolvencyDocument);
       Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.BANK_SOLVENCY);
       bankSolvencyDocument.setDocument(document);
       bankSolvencyRepository.save(bankSolvencyDocument);
    }

}
