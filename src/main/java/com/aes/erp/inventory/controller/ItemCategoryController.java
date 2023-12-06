package com.aes.erp.inventory.controller;


import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/item-categories")
public class ItemCategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/subcategories")
    @ApiOperation(value = "Get Item SubCategories Filtered By Store Type Name and Parent Category Name, With Pagination")
    public ResponseEntity<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(@RequestParam("page") Optional<Integer> page,
                                               @RequestParam("size") Optional<Integer> size,
                                               @RequestParam("storeTypeId")  Optional<Long> storeTypeId,
                                                           @RequestParam("parentCategoryId")  Optional<Long> parentCategoryId
    ){
        return new ResponseEntity<>(
                categoryService.getSubCategoriesFilteredByStoreTypeAndParentCategory(page,size, storeTypeId, parentCategoryId),
                HttpStatus.OK
        );
    }
    @GetMapping
    @ApiOperation(value = "Get Item Categories Filtered By Store Type ID, With Pagination")
    public ResponseEntity<?> getItemCategoriesForStoreType(@RequestParam("page") Optional<Integer> page,
                                                           @RequestParam("size") Optional<Integer> size,
                                                           @RequestParam("storeTypeId")  Optional<Long> storeTypeId
    ){
        return new ResponseEntity<>(
                categoryService.getItemCategoriesForStoreType(page,size, storeTypeId),
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
    public ResponseEntity<?> getMainCategoryList(@RequestParam("name")  Optional<String> name,
                                             @RequestParam("code") Optional<String> code){
        return new ResponseEntity<>(
                categoryService.getCategories(name,code),
                HttpStatus.OK
        );
    }
    @GetMapping("/list")
    public ResponseEntity<?> getCategoryList(@RequestParam("categoryId")  Optional<Long> categoryId,
                                             @RequestParam("name")  Optional<String> name,
                                                @RequestParam("code") Optional<String> code){
        return new ResponseEntity<>(
                categoryService.getSubCategories(categoryId, name,code),
                HttpStatus.OK
        );
    }
    @GetMapping("/subcategory-list")
    public ResponseEntity<?> getCategoryList(@RequestParam("categoryId")  Optional<Long> categoryId){
        return new ResponseEntity<>(
                categoryService.getSubCategoriesByParentId(categoryId),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get Category Detail By ID")
    public ResponseEntity<?> getItemCategory(@ApiParam(value = "Category Id",example = "1", required = true) @PathVariable("id") Long id){
        return new ResponseEntity<>(
                categoryService.getItemCategory(id),
                HttpStatus.OK
        );
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
