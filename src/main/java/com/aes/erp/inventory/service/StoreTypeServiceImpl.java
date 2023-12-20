package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.repository.StoreTypeRepository;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StoreTypeServiceImpl implements StoreTypeService{

    private final StoreTypeRepository storeTypeRepository;
    private final GenericModelMapper mapper;

    public StoreTypeServiceImpl(StoreTypeRepository storeTypeRepository, GenericModelMapper mapper) {
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

    @Override
    public Page<?> getAllStoreTypes(Optional<String> optionalFilter, Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return storeTypeRepository.getAllStoreTypes(pageable, optionalFilter.orElse(""));
    }

    @Override
    public List<?> getAllStoreTypes(Optional<String> filter) {
        return storeTypeRepository.getAllStoreTypes(filter.orElse(""));
    }

    @Override
    public void updateStoreType(Long id, StoreTypeCreateDto createDto) {
        Optional<StoreType> storeTypeOptional = storeTypeRepository.findById(id);
        if(storeTypeOptional.isPresent()){
            StoreType storeType = storeTypeOptional.get();
            storeType.setName(createDto.getName());
            storeTypeRepository.save(storeType);
        }
        else throw new AesException("No store type found with given Id");
    }

    @Override
    @Transactional
    public void deleteStoreType(Long id) {
        Optional<StoreType> storeTypeOptional = storeTypeRepository.findById(id);
        if(storeTypeOptional.isPresent()){
            StoreType storeType = storeTypeOptional.get();
            storeType.setActive(false);
        }
        else throw new AesException("No store type found with given Id");
    }

    @Override
    public StoreType getById(Long id) {
        Optional<StoreType> storeTypeOptional = storeTypeRepository.findById(id);
        if(storeTypeOptional.isEmpty()) throw new AesException("No store type found with given Id");
        return storeTypeOptional.get();
    }
}
