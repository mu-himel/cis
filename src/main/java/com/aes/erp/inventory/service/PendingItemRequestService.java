package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.PendingItemRequestDto;

public interface PendingItemRequestService {
    void createPendingItemRequest(PendingItemRequestDto pRequestDto);
}
