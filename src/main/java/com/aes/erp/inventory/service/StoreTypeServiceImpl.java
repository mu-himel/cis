package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.repository.StoreTypeRepository;
import com.aes.erp.vendor.utils.GenericMapper;
import org.springframework.stereotype.Service;

@Service
public class StoreTypeServiceImpl implements StoreTypeService{

    private final StoreTypeRepository storeTypeRepository;
    private final GenericMapper mapper;

    public StoreTypeServiceImpl(StoreTypeRepository storeTypeRepository, GenericMapper mapper) {
        this.storeTypeRepository = storeTypeRepository;
        this.mapper = mapper;
    }

    @Override
    public StoreTypeGetDto createStoreType(StoreTypeCreateDto createDto) {
        StoreType storeType = new StoreType();
        storeType = mapper.map(createDto, StoreType.class);
        storeType = storeTypeRepository.save(storeType);
        StoreTypeGetDto getDto = new StoreTypeGetDto();
        getDto = mapper.map(storeType, StoreTypeGetDto.class);
        return getDto;
    }
}
