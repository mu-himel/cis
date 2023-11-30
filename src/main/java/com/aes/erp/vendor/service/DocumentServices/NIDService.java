package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.NidResponseDto;
import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;

public interface NIDService {
    NIDDocument create(NIDDocument nidDocument);
    void update(Long documentHolderId, NidResponseDto dto);
}
