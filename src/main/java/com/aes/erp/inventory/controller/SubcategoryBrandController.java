package com.aes.erp.inventory.controller;

import com.aes.erp.inventory.service.BrandServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/item-categories/brands")
public class SubcategoryBrandController {

    private final BrandServiceImpl brandService;

    public SubcategoryBrandController(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/sub-category/{id}")
    public ResponseEntity<?> getBrandsBySubCategoryId(@PathVariable("id") Long id){
        
        return new ResponseEntity<>(brandService.getAllBySubCategoryId(id),HttpStatus.OK);
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get all available brands for a subcategory")
    public ResponseEntity<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(@RequestParam("page") Optional<Integer> page,
                                                                                  @RequestParam("size") Optional<Integer> size
    ){
        return new ResponseEntity<>(
                brandService.getAllBrands(page,size),
                HttpStatus.OK
        );
    }
    @PostMapping()
    @ApiOperation(value = "Create New Brand")
    public ResponseEntity<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(@RequestParam("brand") String name){
        brandService.create(name);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
