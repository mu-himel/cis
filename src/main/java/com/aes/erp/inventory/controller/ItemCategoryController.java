package com.aes.erp.inventory.controller;


import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/item-categories")
public class ItemCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/subcategories")
    @ApiOperation(value = "Get Item SubCategories Filtered By Store Type Name and Parent Category Name, With Pagination")
    public ResponseEntity<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("storeTypeId")  Optional<Long> storeTypeId,
            @RequestParam("parentCategoryId")  Optional<Long> parentCategoryId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("code") Optional<String> code
    ){
        return new ResponseEntity<>(
                categoryService.getSubCategoriesFilteredByStoreTypeAndParentCategory(page,size,
                        storeTypeId, parentCategoryId,
                        name,code),
                HttpStatus.OK
        );
    }
    @GetMapping
    @ApiOperation(value = "Get Item Categories Filtered By Store Type ID, With Pagination")
    public ResponseEntity<?> getItemCategoriesForStoreType(@RequestParam("page") Optional<Integer> page,
                                                           @RequestParam("size") Optional<Integer> size,
                                                           @RequestParam("storeTypeId")  Optional<Long> storeTypeId,
                                                           @RequestParam("name") Optional<String> name,
                                                           @RequestParam("code") Optional<String> code
    ){
        return new ResponseEntity<>(
                categoryService.getItemCategoriesForStoreType(page,size, storeTypeId,name,code),
                HttpStatus.OK
        );
    }

//    @GetMapping("/sub-categories")
//    @ApiOperation(value = "Get Sub Categories With Pagination")
//    public ResponseEntity<?> getSubItemCategories(
//                     @RequestParam("page") Optional<Integer> page,
//                     @RequestParam("size") Optional<Integer> size,
//                     @RequestParam("name")  Optional<String> name,
//                     @RequestParam("code") Optional<String> code,
//                     @RequestParam("currentYearBudget") Optional<BigDecimal> currentYearBudget,
//                     @RequestParam("productCount") Optional<Long> productCount,
//                     @RequestParam("categoryId") Optional<Long> categoryId
//
//    ){
//        return new ResponseEntity<>(
//                categoryService.getItemCategories(page,size, name, code,currentYearBudget,productCount,categoryId),
//                HttpStatus.OK
//        );
//    }

    @GetMapping("/main-categories")
    public ResponseEntity<?> getMainCategoryList(@RequestParam("storeTypeId") Optional<Long> storeTypeId,
                                                 @RequestParam("name") Optional<String> name,
                                                 @RequestParam("code") Optional<String> code
                                                 ){
        return new ResponseEntity<>(
                categoryService.getItemCategoryListForStoreType(storeTypeId,name,code),
                HttpStatus.OK
        );
    }
    @GetMapping("/list")
    public ResponseEntity<?> getCategoryList(
            @RequestParam("categoryId")  Optional<Long> categoryId,
            @RequestParam("storeTypeId")  Optional<Long> storeTypeId,
                                             @RequestParam("name")  Optional<String> name,
                                                @RequestParam("code") Optional<String> code){
        return new ResponseEntity<>(
                categoryService.getSubCategoryListFilteredByStoreTypeAndParentCategory(storeTypeId,categoryId,name,code),
                HttpStatus.OK
        );
    }
    @GetMapping("/subcategory-list")
    public ResponseEntity<?> getCategoryList(@RequestParam("categoryId")  Optional<Long> categoryId,
                                            @RequestParam("categoryName") Optional<String> categoryName){
        return new ResponseEntity<>(
                categoryService.getSubCategoriesByParentIdAndSearchFilter(categoryId, categoryName),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get Category Detail By ID")
    public ResponseEntity<?> getItemCategory(
            @ApiParam(value = "Category Id",example = "1", required = true)
            @PathVariable("id") Long id){
        ItemCategory itemCategory = categoryService.getItemCategory(id).get();
        Map<String, Object> subCategory = entityToMap(itemCategory);
        return new ResponseEntity<>(
                subCategory,
                HttpStatus.OK
        );
    }

    private static Map<String, Object> entityToMap(ItemCategory itemCategory) {
        Map<String,Object> subCategory  = new HashMap<>();
        Map<String,Object> parentCategory = new HashMap<>();
        parentCategory.put("id", itemCategory.getParentCategory().getId());
        parentCategory.put("code", itemCategory.getParentCategory().getCode());
        parentCategory.put("name", itemCategory.getParentCategory().getName());
        List<Map<String,Object>> subcategoryBrands = itemCategory.getSubcategoryBrands().stream().map(sb->{
            Map<String,Object> sbmap = new HashMap<>();
            sbmap.put("id",sb.getId());
            sbmap.put("brand",sb.getBrand());
            return sbmap;
        }).collect(Collectors.toList());
        subCategory.put("id", itemCategory.getId());
        subCategory.put("name", itemCategory.getName());
        subCategory.put("code", itemCategory.getCode());
        subCategory.put("parentCategory",parentCategory);
        subCategory.put("storeType", itemCategory.getStoreType());
        subCategory.put("budgets", itemCategory.getBudgets());
        subCategory.put("attributes", itemCategory.getAttributes());
        subCategory.put("active", itemCategory.getActive());
        subCategory.put("vat", itemCategory.getVat());
        subCategory.put("createdAt", itemCategory.getCreatedAt());
        subCategory.put("subcategoryBrands", subcategoryBrands);
        return subCategory;
    }

    @PostMapping
    @ApiOperation(value = "Create a new Item Category")
    public ResponseEntity<?> createItemCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto){
        categoryService.addCategory(categoryRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update Category Information")
    public ResponseEntity<?> updateItemCategory(@ApiParam(value = "Category Id",
                                                example = "1", required = true) @PathVariable("id") Long id,
                                                @RequestBody CategoryRequestDto categoryRequestDto){
        categoryService.updateCategory(id,categoryRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete Category Information")
    public ResponseEntity<?> deleteItemCategory(@ApiParam(value = "Category Id",
                                            example = "1", required = true) @PathVariable("id") Long id){
        categoryService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{categoryId}/{attributeId}")
    public ResponseEntity<?> deleteCategoryAttribute(
            @ApiParam(value = "Category Id", example = "1", required = true) @PathVariable("categoryId") Long categoryId,
            @ApiParam(value = "Attribute Id", example = "1", required = true) @PathVariable("attributeId") Long attributeId

    ){
        categoryService.deleteAttribute(categoryId,attributeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/next-id")
    @ApiOperation(value = "Get New Category Id")
    public ResponseEntity<?> getNextId(){
        Map<String,Object> response = new HashMap<>();
        response.put("code",categoryService.getNewCategoryCode());
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}
