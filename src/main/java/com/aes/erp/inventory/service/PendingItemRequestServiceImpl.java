package com.aes.erp.inventory.service;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.PendingItemRequestDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.PendingItemRequestRepository;

@Service
public class PendingItemRequestServiceImpl implements PendingItemRequestService{

    private static final Integer PAGE_SIZE = 10;

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
        pir.setRequestNo(getNextItemCode());
        pir.setSubCategory(subCat);
        pir.setCategory(subCat.getParentCategory());
        pir.setBrand(brandOp.get());
        pendingItemRequestRepository.save(pir);
        
    }

    private String getNextItemCode() {
        Optional<PendingItemRequest> pirOp = pendingItemRequestRepository.findMaxOrderById();
        if(pirOp.isPresent()){
            PendingItemRequest pir = pirOp.get();
            Long newProductId = pir.getId() + 1;
            return String.format("%05d",newProductId);
        }
        return String.format("%05d",1);
    }

    @Override
    public Optional<?> getDetail(Long id) {
        return pendingItemRequestRepository.findById(id,PendingItemRequest.class);
    }

    @Override
    public Page<?> getPage(Optional<Integer> page, Optional<Integer> size) {
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(PAGE_SIZE));
        return pendingItemRequestRepository.findAllPendingItemRequests(pageable);
    }

    
    
}
