package com.aes.erp.vendor.service.DocumentHolderServices;

import com.aes.erp.vendor.document_response_dto.*;

public interface DocumentHolderService {
    DocumentHolderResponseDto create(Long id, DocumentHolderRequestDto dto);
    DocumentHolderResponseDto getDocumentHolderById(Long id);
    MisMatchResponseDto findMisMatch(MisMatchDto misMatchDto, Long id);

    void addHolderDetails(DetailsDTO dto, Long userId, Long documentHolderId);
}
