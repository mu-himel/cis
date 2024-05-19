package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.dto.response.SubCategory;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;

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


    Page<?> getSubCategoriesFilteredByParentCategory(Optional<Integer> page, Optional<Integer> size,
                                                                 Optional<Long> parentCategoryId,
                                                                 Optional<String> name,
                                                                 Optional<String> code
                                                                 );

    Page<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Integer> page, Optional<Integer> size,
                                                                 Optional<Long> storeTypeId,
                                                                 Optional<Long> parentCategoryId,
                                                                 Optional<String> name,
                                                                 Optional<String> code
                                                                 );

    List<?> getSubCategoryListFilteredByParentCategory(Optional<Long> parentCategoryId,
                                                                   Optional<String> name,
                                                                   Optional<String> code);

    List<?> getSubCategoryListFilteredByStoreTypeAndParentCategory(Optional<Long> storeTypeId,
                                                                   Optional<Long> parentCategoryId,
                                                                   Optional<String> name,
                                                                   Optional<String> code);

    Page<?> getItemCategoriesForStoreType(Optional<Integer> page, Optional<Integer> size,
                              Optional<Long> id, Optional<String> name, Optional<String> code);
    List<?> getItemCategoryListForStoreType(Optional<Long> id, Optional<String> name, Optional<String> code);

    Page<?> getItemCategories( Optional<Integer> page, Optional<Integer> size,
                               Optional<String> name, Optional<String> code,
                               Optional<BigDecimal> currentYearBudget, Optional<Long> productCount,
                               Optional<Long> categoryId
    );

    void deleteCategory(Long id);

    List<?> getCategories(Optional<String> name, Optional<String> code);
    List<?> getSubCategoriesByParentIdAndSearchFilter(Optional<Long> categoryId, Optional<String> categoryName);


    List<?> getSubCategories(Optional<Long>categoryId, Optional<String> name, Optional<String> code);

    String getNewCategoryCode();

    void deleteAttribute(Long categoryId, Long attributeId);
    Optional<ItemCategory> getCategoryForAVendor(Long vendorId, Long Id);
    List<SubCategory> getCategoriesForVendor(Long vendorId);
    Optional<ItemCategory> findRootReferenceItem(Long Id);

    List<ItemCategory> existCategoryByNameIgnoreCase(String category_name);

    List<ItemCategory> existCategoryBySubCatNameIgnoreCase(String category_name);

    Optional<CategoryAttribute> getCategoryAttributeValueBySubCatAndAttributeType(Long subCatId, String attributeType);
    void bulkImport(Organization org, Long userId, Long warehouseId, Long storeId,Long parentCategoryId, List<Long> id);
    List<ItemCategory> getAllSubCategories();
}
