package com.aes.erp.inventory.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.PendingItemRequestDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.PendingItemRequestRepository;

@Service
public class PendingItemRequestServiceImpl implements PendingItemRequestService{

    @Autowired
    private PendingItemRequestRepository pendingItemRequestRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public void createPendingItemRequest(PendingItemRequestDto pRequestDto) {
    
        PendingItemRequest pir = pRequestDto.getEntity();

        Optional<Brand> brandOp =brandRepository.findByName(pRequestDto.getBrand());
        if(brandOp.isEmpty()){
            throw new AesException("Sorry! Brand not specified");
        }

        Optional<ItemCategory> catOp = categoryService.existByCode(pRequestDto.getSubCategoryCode());
        if(catOp.isEmpty()){
            throw new AesException("Sorry! Sub Category not specified");
        }
        ItemCategory subCat = catOp.get();
        pir.setSubCategory(subCat);
        pir.setCategory(subCat.getParentCategory());
        pir.setBrand(brandOp.get());
    

        pendingItemRequestRepository.save(pir);
        
    }
    
}
