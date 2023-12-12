package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.entity.SubcategoryBrand;
import com.aes.erp.inventory.repository.*;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StoreTypeRepository storeTypeRepository;

    @Autowired
    private CategoryBudgetRepository categoryBudgetRepository;

    private final GenericModelMapper genericModelMapper;
    private final SubcategoryBrandRepository subcategoryBrandRepository;
    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;

    public CategoryServiceImpl(GenericModelMapper genericModelMapper, SubcategoryBrandRepository subcategoryBrandRepository) {
        this.genericModelMapper = genericModelMapper;
        this.subcategoryBrandRepository = subcategoryBrandRepository;
    }

    @Override
    @Transactional
    public void addCategory(CategoryRequestDto categoryRequestDto) {
        ItemCategory category = categoryRequestDto.getEntity();
        if(categoryRepository.existsByCode(category.getCode())){
            throw new AesException("Category code already exist");
        }
        if(categoryRequestDto.getParentCategory() != null){
            Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(categoryRequestDto.getParentCategory().getId());
            if(itemCategoryOptional.isPresent()) category.setParentCategory(itemCategoryOptional.get());
        }
        if(categoryRequestDto.getStoreType().getId() != null){
            Optional<StoreType> storeType = storeTypeRepository.findById(categoryRequestDto.getStoreType().getId());
            if(storeType.isPresent())category.setStoreType(storeType.get());
        }
        if(categoryRequestDto.getAttributes()!=null && categoryRequestDto.getAttributes().size()>0){
            category.setAttributes(categoryRequestDto.getAttributes().stream().map(categoryAttribute -> {
                categoryAttribute.setCategory(category);
                return categoryAttribute;
            }).collect(Collectors.toList()));
        }
        List<SubcategoryBrand> brands = genericModelMapper.mapDtoListToEntityList(categoryRequestDto.getBrands(), SubcategoryBrand.class);
        if(brands!=null && !brands.isEmpty()){
            category.setBrands(brands.stream().map(brand -> {
                brand.setCategory(category);
                return brand;
            }).collect(Collectors.toList()));
        }
        category.setCreatedAt(Instant.now().toEpochMilli());
        categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(id);
        if(itemCategoryOptional.isEmpty()){
            throw new AesException("Category Not Found");
        }

        ItemCategory itemCategory = itemCategoryOptional.get();
        if(!itemCategory.getCode().equalsIgnoreCase(categoryRequestDto.getCode())){
            throw new AesException("Category Code should be unique");
        }

        if(itemCategory.getParentCategory()!=null){
            if(categoryRequestDto.getParentCategory()==null || categoryRequestDto.getParentCategory().getId()==null){
                throw new AesException("Parent Category Id missing");
            }
        }

        if(categoryRequestDto.getName()!=null) {
            itemCategory.setName(categoryRequestDto.getName());
        }

        if(categoryRequestDto.getStoreType().getId() != null){
            Optional<StoreType> storeType = storeTypeRepository.findById(categoryRequestDto.getStoreType().getId());
            if(storeType.isPresent())itemCategory.setStoreType(storeType.get());
        }

        if(categoryRequestDto.getAttributes()!=null && categoryRequestDto.getAttributes().size()>0){
            itemCategory.setAttributes(categoryRequestDto.getAttributes().stream().map(categoryAttribute -> {
                categoryAttribute.setCategory(itemCategory);
                return categoryAttribute;
            }).collect(Collectors.toList()));
        }

        if(categoryRequestDto.getEntity().getParentCategory()!=null) {
            itemCategory.setParentCategory(categoryRequestDto.getEntity().getParentCategory());
        }
        categoryRepository.save(itemCategory);
    }

    @Override
    public Optional<ItemCategory> existByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Override
    public Optional<ItemCategory> getItemCategory(Long id) {
        Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(id);
        return itemCategoryOptional;
    }

    @Override
    public Page<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Integer> page, Optional<Integer> size, Optional<Long> storeTypeId, Optional<Long> parentCategoryID) {
//        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        return categoryRepository.findAllBySubCategoryFilteredByStoreTypeAndParentCategory(storeTypeId.orElse(null), parentCategoryID.orElse(null), pageable);
    }

    @Override
    public List<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Long> storeTypeId, Optional<Long> parentCategoryId) {
        return categoryRepository.findAllBySubCategoryFilteredByStoreTypeAndParentCategory(storeTypeId.orElse(null),
                parentCategoryId.orElse(null));
    }

    @Override
    public Page<?> getItemCategoriesForStoreType(Optional<Integer> page, Optional<Integer> size,
                                        Optional<Long> store_type_id) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10),sort);

            return categoryRepository.findAllByItemCategoryWithSubCategoryCount(store_type_id.orElse(null), pageable);
    }

    @Override
    public Page<?> getItemCategories( Optional<Integer> page, Optional<Integer> size,
                                      Optional<String> name, Optional<String> code,
                                      Optional<BigDecimal> currentYearBudget, Optional<Long> productCount,
                                      Optional<Long> categoryId
                                      ) {



        Integer year  = LocalDate.now().getYear();
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        
        Page<?> result = null;
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10),sort);


        result = categoryRepository.findAllSubCategories(
                            name.orElse(null),
                            code.orElse(null),
                            currentYearBudget.orElse(null),
                            productCount.orElse(null),
                            categoryId.orElse(null),
                            year,pageable);

        return result;
    }

    @Override
    public List<?> getItemCategoriesForStoreType(Optional<Long> id) {

        return categoryRepository.findAllByItemCategoryWithSubCategoryCount(id.orElse(null));
    }

    @Override
    public List<?> getCategories(Optional<String> name, Optional<String> code) {

        return categoryRepository.findAllMainCategories(name.orElse(null),code.orElse(null));
    }

    @Override
    public List<?> getSubCategoriesByParentId(Optional<Long> categoryId) {
        return categoryRepository.findAllSubCategories(categoryId.orElse(null));
    }

    @Override
    public List<?> getSubCategories(Optional<Long> id, Optional<String> name, Optional<String> code) {

        return categoryRepository.findAllSubCategories(
                id.orElse(null),
                name.orElse(null),
                code.orElse(null));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(id);
        if(itemCategoryOptional.isPresent()){

            Optional<Long> countOptional = categoryRepository.countAllByParentCategoryAndActive(
                    itemCategoryOptional.get(),true);
            if(countOptional.isPresent() && countOptional.get() > 0){
                throw new AesException("Sorry! Unable to delete, Category already used in Child Category");
            }

            ItemCategory itemCategory = itemCategoryOptional.get();
            itemCategory.setActive(false);
            categoryRepository.save(itemCategory);
        }
    }

    @Override
    public String getNewCategoryCode() {
        Optional<ItemCategory> icOp = categoryRepository.findMaxOrderById();
        if(icOp.isPresent()){
            ItemCategory ic = icOp.get();
            Long newProductId = ic.getId() + 1;
            return String.format("%05d",newProductId);
        }
        return String.format("%05d",1);
    }

    @Override
    @Transactional
    public void deleteAttribute(Long categoryId, Long attributeId) {
        Optional<ItemCategory> icOp = categoryRepository.findById(categoryId);
        if(icOp.isPresent()){
            categoryAttributeRepository.deleteByIdAndCategoryId(attributeId,categoryId);

        }

    }
}
