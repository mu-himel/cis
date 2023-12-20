package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;
import com.aes.erp.inventory.entity.StoreType;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface StoreTypeService {
    StoreTypeGetDto createStoreType(StoreTypeCreateDto createDto);
    Page<?> getAllStoreTypes(Optional<String> filter, Optional<Integer> page, Optional<Integer> size);
    List<?> getAllStoreTypes(Optional<String> filter);
    void updateStoreType(Long id, StoreTypeCreateDto createDto);
    void deleteStoreType(Long id);
    StoreType getById(Long id);
}
