package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface StoreTypeService {
    StoreTypeGetDto createStoreType(StoreTypeCreateDto createDto);
    Page<?> getAllStoreTypes(Optional<String> filter, Optional<Integer> page, Optional<Integer> size);
    void updateStoreType(Long id, StoreTypeCreateDto createDto);
    void deleteStoreType(Long id);
}
