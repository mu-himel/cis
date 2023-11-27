package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;

public interface StoreTypeService {
    StoreTypeGetDto createStoreType(StoreTypeCreateDto createDto);
}
