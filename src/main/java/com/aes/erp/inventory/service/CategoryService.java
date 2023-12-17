package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CategoryService {

    void addCategory(CategoryRequestDto categoryRequestDto);
    ItemCategory addCategoryFromCategoryEntity(ItemCategory itemCategory);

    void updateCategory(Long id,CategoryRequestDto categoryRequestDto);
    Optional<ItemCategory> existByCode(String Code);


    Optional<ItemCategory> getItemCategory(Long id);


    Page<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Integer> page, Optional<Integer> size,
                                                                 Optional<Long> storeTypeId, Optional<Long> parentCategoryId);

    List<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Long> storeTypeId, Optional<Long> parentCategoryId);
    Page<?> getItemCategoriesForStoreType(Optional<Integer> page, Optional<Integer> size,
                              Optional<Long> id);
    List<?> getItemCategoriesForStoreType(Optional<Long> id);

    Page<?> getItemCategories( Optional<Integer> page, Optional<Integer> size,
                               Optional<String> name, Optional<String> code,
                               Optional<BigDecimal> currentYearBudget, Optional<Long> productCount,
                               Optional<Long> categoryId
    );

    void deleteCategory(Long id);

    List<?> getCategories(Optional<String> name, Optional<String> code);
    List<?> getSubCategoriesByParentId(Optional<Long> categoryId);


    List<?> getSubCategories(Optional<Long>categoryId, Optional<String> name, Optional<String> code);

    String getNewCategoryCode();

    void deleteAttribute(Long categoryId, Long attributeId);
    Optional<ItemCategory> getCategoryForAVendor(Long vendorId, Long Id);
    Optional<ItemCategory> findRootReferenceItem(Long Id);
}
