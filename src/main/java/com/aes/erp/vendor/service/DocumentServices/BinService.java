package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.BinResponseDto;
import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import org.springframework.stereotype.Service;

public interface BinService {
    BINDocument create(BINDocument binDocument);
    void update(Long documentHolderId, BinResponseDto dto);
}
