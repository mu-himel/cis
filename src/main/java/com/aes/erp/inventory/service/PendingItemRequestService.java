package com.aes.erp.inventory.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;

import com.aes.erp.inventory.controller.PendingItemReqController.PendingAttributesDto;
import com.aes.erp.inventory.dto.request.PendingAttributeDto;
import com.aes.erp.inventory.dto.request.PendingBrandDto;
import com.aes.erp.inventory.dto.request.PendingItemRequestDto;

public interface PendingItemRequestService {
    Map<String, Object> createPendingItemRequest(PendingItemRequestDto pRequestDto);

    Page<?> getPage(Optional<Long> categoryId, Optional<Long> subCategoryId, Optional<Integer> page, Optional<Integer> size);
    Optional<?> getDetail(Long id);
    List<?> getPendingBrands(Long subCatId);
    void createPendingBrand(PendingBrandDto pendingBrandDto);
    void createPendingAttribute(PendingAttributesDto pendingAttributesDto);
    List<?> getPendingAttributes(Long subCatId);
    void deletePendingBrand(Long id);
    void deletePendingAttribute(Long id);
    void deletePendingBrands(List<Long> id);
    void deletePendingAttributes(List<Long> id);
    void deletePendingItemRequest(Long id);
    void rejectPendingItem(Long id);
}
