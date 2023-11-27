package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.TinResponseDto;
import com.aes.erp.vendor.entity.DocmentEntities.TINDocument;

public interface TINService {
    TINDocument create(TINDocument tinDocument);
    void update(Long documentHolderId, TinResponseDto dto);
}
