package com.aes.erp.inventory.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.aes.erp.inventory.dto.request.PendingItemRequestDto;

public interface PendingItemRequestService {
    void createPendingItemRequest(PendingItemRequestDto pRequestDto);
    Page<?> getPage(Optional<Integer>page, Optional<Integer> size);
    Optional<?> getDetail(Long id);
}
