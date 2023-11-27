package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.BankSolvencyDto;
import com.aes.erp.vendor.entity.DocmentEntities.BankSolvencyDocument;

public interface BankSolvencyService {
    BankSolvencyDocument create(BankSolvencyDocument bankSolvencyDocument);
    void update(Long documentHolderId, BankSolvencyDto dto);
}
