package com.aes.erp.inventory.controller;


import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.dto.request.ErpBulkImportDto;
import com.aes.erp.inventory.dto.request.ImportCategoryScmIdUpdateDto;
import com.aes.erp.inventory.dto.request.MergePendingCategoryDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
                categoryService.getSubCategoriesFilteredByParentCategory(page,size,
                        parentCategoryId,name,code),
                HttpStatus.OK
        );
    }

    @GetMapping("/subcategories/pending")
    @ApiOperation(value = "Get Pending SubCategories  With Pagination")
    public ResponseEntity<?> getPendingSubCategoriesFilteredByStoreTypeAndParentCategory(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("parentCategoryId")  Optional<Long> parentCategoryId,
            @RequestParam("name") Optional<String> name,
            @RequestParam("code") Optional<String> code
    ){
        return new ResponseEntity<>(
                categoryService.getPendingSubCategoryList(
                        parentCategoryId,name,code,page,size),
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
    public ResponseEntity<?> getMainCategoryList(
                                                 @RequestAttribute Optional<ClaimResponseDto> loggedInUser,
                                                 @RequestParam("storeTypeId") Optional<Long> storeTypeId,
                                                 @RequestParam("name") Optional<String> name,
                                                 @RequestParam("code") Optional<String> code
                                                 ){
        return new ResponseEntity<>(
                categoryService.getItemCategoryListForStoreType( loggedInUser,storeTypeId,name,code),
                HttpStatus.OK
        );
    }

    @GetMapping("/main-categories/pending")
    public ResponseEntity<?> getPendingMainCategoryList(
                                                @RequestParam("page") Optional<Integer> page,
                                                @RequestParam("size") Optional<Integer> size,
                                                 @RequestParam("name") Optional<String> name,
                                                 @RequestParam("code") Optional<String> code
    ){
        return new ResponseEntity<>(
                categoryService.getPendingItemCategoryList(name,code,page,size),
                HttpStatus.OK
        );
    }

    @GetMapping("/list")
    public ResponseEntity<?> getCategoryList(
            @RequestAttribute ClaimResponseDto loggedInUser,
            @RequestParam("categoryId")  Optional<Long> categoryId,
            @RequestParam("storeTypeId")  Optional<Long> storeTypeId,
            @RequestParam("name")  Optional<String> name,
            @RequestParam("code") Optional<String> code ){
        return new ResponseEntity<>(
                categoryService.getSubCategoryListFilteredByStoreTypeAndParentCategory(storeTypeId,categoryId,name,code, loggedInUser),
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

    private  Map<String, Object> entityToMap(ItemCategory itemCategory) {
        Map<String,Object> catDetail  = new HashMap<>();
        Map<String,Object> parentCategory =null;
        if(itemCategory.getParentCategory()!=null){
            parentCategory = new HashMap<>();
            parentCategory.put("id", itemCategory.getParentCategory().getId());
            parentCategory.put("code", itemCategory.getParentCategory().getCode());
            parentCategory.put("name", itemCategory.getParentCategory().getName());
        }
        
        List<Map<String,Object>> subcategoryBrands = itemCategory.getSubcategoryBrands().stream().map(sb->{
            Map<String,Object> sbmap = new HashMap<>();
            sbmap.put("id",sb.getId());
            sbmap.put("brand",sb.getBrand());
            return sbmap;
        }).collect(Collectors.toList());
        catDetail.put("id", itemCategory.getId());
        catDetail.put("name", itemCategory.getName());
        catDetail.put("categoryStatus",itemCategory.getCategoryStatus());
        catDetail.put("employee", itemCategory.getRequesterName());
        catDetail.put("organization", itemCategory.getOrganization());
        catDetail.put("code", itemCategory.getCode());
        Long subCatId=null;
        Long catId=null;
        if(itemCategory.getParentCategory()==null){
            catId = itemCategory.getId();
            catDetail.put("subCategoryQty",categoryService.getSubCategoryCount(itemCategory.getId()));
        }

        if(itemCategory.getParentCategory()!=null){
            subCatId = itemCategory.getId();
            catId = itemCategory.getParentCategory().getId();
        }



        catDetail.put("productQty", categoryService.getProductQtyByCategoryAndSubCategory(catId,subCatId));
        catDetail.put("parentCategory",parentCategory);
        catDetail.put("storeType", itemCategory.getStoreTypeId());
        catDetail.put("budgets", itemCategory.getBudgets());
        catDetail.put("attributes", itemCategory.getAttributes());
        catDetail.put("active", itemCategory.getActive());
        catDetail.put("vat", itemCategory.getVat());
        catDetail.put("createdAt", itemCategory.getCreatedAt());
        catDetail.put("subcategoryBrands", subcategoryBrands);
        return catDetail;
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> importToErp(
        @RequestAttribute String token,
        @RequestAttribute Organization organization,
        @RequestBody ErpBulkImportDto erpImportDto){
        categoryService.bulkImport(token, organization, erpImportDto.getUserId(), erpImportDto.getWarehouseId(),
                erpImportDto.getWarehouseStoreId(), erpImportDto.getParentCategoryId(), erpImportDto.getId(), erpImportDto.getIsSync());
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping
    @ApiOperation(value = "Create a new Item Category")
    public ResponseEntity<?> createItemCategory(
        @RequestAttribute("organization") Optional<Organization> organization,
        @RequestBody @Valid CategoryRequestDto categoryRequestDto){
        if (organization != null && organization.isPresent()) {
            categoryRequestDto.setOrganization(organization.get());
        }
        Map<String, Object> objectMap = categoryService.addCategory(categoryRequestDto);
        HttpHeaders headers = new HttpHeaders();
        if (objectMap.get("id") == null) {
            throw new RuntimeException(objectMap.get("message").toString());
        }
        headers.set("id", objectMap.get("id").toString());
        headers.set("code", objectMap.get("code").toString());
        headers.set("message", objectMap.get("message").toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update Category Information")
    public ResponseEntity<?> updateItemCategory(@ApiParam(value = "Category Id",
                                                example = "1", required = true) @PathVariable("id") Long id,
                                                @RequestBody CategoryRequestDto categoryRequestDto){
        categoryService.updateCategory(id,categoryRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/update-scm-id")
    public ResponseEntity<?> updateScmCategotyId(@RequestBody List<ImportCategoryScmIdUpdateDto> scmIdList) {
        categoryService.updateCategoryScmId(scmIdList);
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
    public ResponseEntity<?> getNextId(
            @RequestParam("key") String key,
            @RequestParam("prefix") String prefix,
            @RequestParam("categoryId") Optional<Long> categoryId
    ){
        Map<String,Object> response = new HashMap<>();
        response.put("code",categoryService.getNewCategoryCode(key,prefix,categoryId));
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }

    @PutMapping("/merge-pending-category/{id}")
    public ResponseEntity<?> mergePendingCategory(@PathVariable Long id, @RequestBody MergePendingCategoryDto mergePendingCategoryDto){
        categoryService.mergePendingCategory(id,mergePendingCategoryDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/merge-pending-subcategory/{id}")
    public ResponseEntity<?> mergePendingSubcategory(@PathVariable Long id, @RequestBody MergePendingCategoryDto mergePendingCategoryDto){
        categoryService.mergePendingCategory(id,mergePendingCategoryDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/reject-pending/{id}")
    public ResponseEntity<?> rejectPendingCategory(@PathVariable Long id){
        categoryService.rejectPendingCategory(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


   

    @GetMapping("/main-categories/all")
    public ResponseEntity<?> getAllMainCategoryList(
                                                 @RequestParam("name") Optional<String> name,
                                                 @RequestParam("code") Optional<String> code
                                                 ){
        return new ResponseEntity<>(
                categoryService.getAllItemCategoryList(name,code),
                HttpStatus.OK
        );
    }

    @GetMapping("/subcategories/all")
    public ResponseEntity<?> getAllSubCategories(
            @RequestParam("categoryId")  Optional<Long> categoryId,
            @RequestParam("name")  Optional<String> name,
            @RequestParam("code") Optional<String> code){
        return new ResponseEntity<>(
                categoryService.getSubCategoryListFilteredByParentCategoryNameOrCode(categoryId,name,code),
                HttpStatus.OK
        );
    }
}
