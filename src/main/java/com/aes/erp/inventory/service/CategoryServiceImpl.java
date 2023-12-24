package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.SubCategoryBrand;
import com.aes.erp.inventory.repository.*;
import com.aes.erp.vendor.entity.VendorSubCategory;
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
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    @Autowired
    private StoreTypeRepository storeTypeRepository;

    @Autowired
    private CategoryBudgetRepository categoryBudgetRepository;

    private final GenericModelMapper genericModelMapper;
    private final SubcategoryBrandRepository subcategoryBrandRepository;
    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;

    public CategoryServiceImpl(BrandRepository brandRepository, GenericModelMapper genericModelMapper, SubcategoryBrandRepository subcategoryBrandRepository) {
        this.brandRepository = brandRepository;
        this.genericModelMapper = genericModelMapper;
        this.subcategoryBrandRepository = subcategoryBrandRepository;
    }

    public void addBrandToSubCategory(CategoryRequestDto dto, ItemCategory category){
        if(dto.getBrands() != null){
            Set<SubCategoryBrand> subCategoryBrands = new HashSet<>();
            for(String brandName: dto.getBrands()) {
                Optional<SubCategoryBrand> existingBrandOptional = subcategoryBrandRepository.getBrandByNameAndSubCategoryId(brandName, category.getId());
                if(existingBrandOptional.isEmpty()){
                    SubCategoryBrand subcategoryBrand = new SubCategoryBrand();
                    Optional<Brand> brandOptional = brandRepository.findByName(brandName);
                    Brand brandToBeAdded = new Brand();
                    if(brandOptional.isEmpty()){
                        brandToBeAdded.setName(brandName);
                        brandToBeAdded = brandRepository.save(brandToBeAdded);
                    }
                    else brandToBeAdded = brandOptional.get();
                    subcategoryBrand.setBrand(brandToBeAdded);
                    subcategoryBrand.setSubcategory(category);
                    subcategoryBrand = subcategoryBrandRepository.save(subcategoryBrand);
                    subCategoryBrands.add(subcategoryBrand);
                }
            }
            category.setSubcategoryBrands(subCategoryBrands);
        }
        //If Its a subcategory Brands Must be included.
        if(category.getParentCategory() != null && dto.getBrands() == null) throw new AesException("Brands must included to create a subcategory");
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
        category = categoryRepository.save(category);
        addBrandToSubCategory(categoryRequestDto, category);
        if(categoryRequestDto.getAttributes()!=null && categoryRequestDto.getAttributes().size()>0){
            ItemCategory finalCategory = category;
            category.setAttributes(categoryRequestDto.getAttributes().stream().map(categoryAttribute -> {
                categoryAttribute.setCategory(finalCategory);
                return categoryAttribute;
            }).collect(Collectors.toList()));
        }
        category.setCreatedAt(Instant.now().toEpochMilli());
        categoryRepository.save(category);
    }

    @Override
    public ItemCategory addCategoryFromCategoryEntity(ItemCategory itemCategory) {
        return categoryRepository.save(itemCategory);
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
        removeBrandsForSubCategory(itemCategory, categoryRequestDto.getBrands());
        addBrandToSubCategory(categoryRequestDto, itemCategory);
        if(categoryRequestDto.getAttributes()!=null && categoryRequestDto.getAttributes().size()>0){
            ItemCategory finalItemCategory = itemCategory;
            itemCategory.setAttributes(categoryRequestDto.getAttributes().stream().map(categoryAttribute -> {
                categoryAttribute.setCategory(finalItemCategory);
                return categoryAttribute;
            }).collect(Collectors.toList()));
        }
        if(categoryRequestDto.getEntity().getParentCategory()!=null) {
            itemCategory.setParentCategory(categoryRequestDto.getEntity().getParentCategory());
        }
        categoryRepository.save(itemCategory);
    }
    public void removeBrandsForSubCategory(ItemCategory itemCategory, List<String> brandNames){
        if(brandNames != null){
            List<SubCategoryBrand> subCategoryBrandsToBeDeleted = new ArrayList<>();
            for (SubCategoryBrand subCategoryBrand : itemCategory.getSubcategoryBrands()) {
                if(!brandNames.contains(subCategoryBrand.getBrand().getName())){
                    subCategoryBrandsToBeDeleted.add(subCategoryBrand);
                }
            }
            for(SubCategoryBrand subCategoryBrand: subCategoryBrandsToBeDeleted){
                subcategoryBrandRepository.delete(subCategoryBrand);
                itemCategory.getSubcategoryBrands().remove(subCategoryBrand);
            }
        }
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
    public Page<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Integer> page, Optional<Integer> size,
                                                                    Optional<Long> storeTypeId,
                                                                    Optional<Long> parentCategoryId,
                                                                    Optional<String> name,
                                                                    Optional<String> code) {
//        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        return categoryRepository.findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
                storeTypeId.orElse(null),
                parentCategoryId.orElse(null),
                name.orElse(null),code.orElse(null),
                pageable);
    }

    @Override
    public List<?> getSubCategoryListFilteredByStoreTypeAndParentCategory(Optional<Long> storeTypeId,
                                                                          Optional<Long> parentCategoryId,
                                                                          Optional<String> name,
                                                                          Optional<String> code) {
        return categoryRepository.findAllBySubCategoryFilteredByStoreTypeAndParentCategory(storeTypeId.orElse(null),
                parentCategoryId.orElse(null),
                name.orElse(null), code.orElse(null));
    }

    @Override
    public Page<?> getItemCategoriesForStoreType(Optional<Integer> page, Optional<Integer> size,
                                        Optional<Long> store_type_id, Optional<String> name,
                                                 Optional<String> code) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10),sort);

            return categoryRepository.findAllByItemCategoryWithSubCategoryCount(
                    store_type_id.orElse(null),
                    name.orElse(null), code.orElse(null),
                    pageable);
    }

    @Override
    public List<?> getItemCategoryListForStoreType(Optional<Long> id, Optional<String> name, Optional<String> code) {
        return categoryRepository.findAllByItemCategoryWithSubCategoryCount(id.orElse(null),
                name.orElse(null),code.orElse(null));
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
    public List<?> getCategories(Optional<String> name, Optional<String> code) {

        return categoryRepository.findAllMainCategories(name.orElse(null),code.orElse(null));
    }

    @Override
    public List<?> getSubCategoriesByParentIdAndSearchFilter(Optional<Long> categoryId, Optional<String> categoryName) {
        return categoryRepository.findAllSubCategories(categoryId.orElse(null), categoryName.orElse(""));
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

    @Override
    public Optional<ItemCategory> getCategoryForAVendor(Long vendorId, Long Id) {
       return categoryRepository.findSavedCategoryForVendor(vendorId, Id);
    }

    @Override
    public Optional<ItemCategory> findRootReferenceItem(Long Id) {
        return categoryRepository.findById(Id);
    }
}
