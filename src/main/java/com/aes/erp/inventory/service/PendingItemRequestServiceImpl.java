package com.aes.erp.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.controller.PendingItemReqController.PendingAttributesDto;
import com.aes.erp.inventory.dto.request.PendingAttributeDto;
import com.aes.erp.inventory.dto.request.PendingBrandDto;
import com.aes.erp.inventory.dto.request.PendingItemRequestDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.PendingAttribute;
import com.aes.erp.inventory.entity.PendingBrand;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.PendingAttributeRepository;
import com.aes.erp.inventory.repository.PendingBrandRepository;
import com.aes.erp.inventory.repository.PendingItemRequestRepository;

@Service
public class PendingItemRequestServiceImpl implements PendingItemRequestService{

    private static final Integer PAGE_SIZE = 10;

    @Autowired
    private PendingItemRequestRepository pendingItemRequestRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private PendingBrandRepository pendingBrandRepository;

    @Autowired
    private PendingAttributeRepository pendingAttributeRepository;

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
        pir.setAttributes(pRequestDto.getAttributes().stream().map(pia->{
            pia.setId(null);
            pia.setPendingItemRequest(pir);
            return pia;
        }).collect(Collectors.toList()));
        pir.setOrganization(new Organization(pRequestDto.getOrganizationId()));
        pendingItemRequestRepository.save(pir);
        
    }

    private String getNextItemCode() {
        Optional<Long> pirOp = pendingItemRequestRepository.findMaxOrderById();
        if(pirOp.isPresent()){
            Long pir = pirOp.get();
            Long newProductId = pir + 1;
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

    @Override
    public List<?> getPendingBrands(Long subCatId) {
        return pendingBrandRepository.findAllBySubCategoryId(subCatId);
    }

    @Override
    @Transactional
    public void createPendingBrand(PendingBrandDto pendingBrandDto) {
        PendingBrand pendingBrand = pendingBrandDto.getEntity();
        List<PendingBrand> pendingBrands = pendingBrandRepository.findAllByBrandName(pendingBrand.getBrandName());
        if(pendingBrands.size()>0){
            throw new AesException("Brand Request Already Pending");
        }
        pendingBrandRepository.save(pendingBrand);
    }

    

    @Override
    @Transactional
    public void deletePendingBrand(Long id) {
        Optional<PendingBrand> pendingBrandOp = pendingBrandRepository.findById(id);
        if(pendingBrandOp.isPresent()){
            PendingBrand pendingBrand = pendingBrandOp.get();
            pendingBrandRepository.delete(pendingBrand);
        }
    }

    

    @Override
    @Transactional
    public void deletePendingBrands(List<Long> id) {
        if(id.isEmpty()){
            throw new AesException("Sorry! delete not possible list is empty");
        }
        pendingBrandRepository.deleteAllById(id);
    }


    @Override
    @Transactional
    public void deletePendingAttributes(List<Long> ids) {
        if(ids.isEmpty()){
            throw new AesException("Sorry! delete not possible list is empty");
        }
        pendingAttributeRepository.deleteAllById(ids);
        
    }

    @Override
    @Transactional
    public void deletePendingItemRequest(Long id) {
        pendingItemRequestRepository.deleteById(id);
    }

    @Override
    public List<?> getPendingAttributes(Long subCatId) {
        return pendingAttributeRepository.findAllBySubCategoryId(subCatId);
    }

    @Override
    @Transactional
    public void createPendingAttribute(PendingAttributesDto pendingAttributesDto) {
        List<PendingAttribute> pendingAttributes = new ArrayList<>();
        pendingAttributesDto.pendingAttributes().stream().forEach(pendingAttributeDto->{
            PendingAttribute pendingAttribute = pendingAttributeDto.getEntity();
            Optional<PendingAttribute> pendingAttrOp = pendingAttributeRepository
                .findAllBySubCategoryIdAndAttributeTypeAndAttributeValue(pendingAttribute.getSubCategory().getId(),
            pendingAttribute.getAttributeType(),pendingAttribute.getAttributeValue());
            if(pendingAttrOp.isEmpty()){

                Optional<CategoryAttribute> catAttrOp = categoryService.getCategoryAttributeValueBySubCatAndAttributeType(pendingAttribute.getSubCategory().getId(),
                pendingAttribute.getAttributeType());
                if(catAttrOp.isPresent()){
                    if(catAttrOp.get().getAttributeValue().toLowerCase().contains(pendingAttribute.getAttributeValue().toLowerCase())){
                        throw new AesException("Sorry! Attribute already exist");
                    }
                }

                pendingAttributes.add(pendingAttribute);
            }else{
                throw new AesException("Sorry! Attribute already exist as pending");
            }
            
        });
        
        pendingAttributeRepository.saveAll(pendingAttributes);
    }

    @Override
    @Transactional
    public void deletePendingAttribute(Long id) {
        Optional<PendingAttribute> pendAttrOptional = pendingAttributeRepository.findById(id);
        if(pendAttrOptional.isPresent()){

            PendingAttribute pendingAttribute = pendAttrOptional.get();
            pendingAttributeRepository.delete(pendingAttribute);
        }
        
    }

    

    
    
}
