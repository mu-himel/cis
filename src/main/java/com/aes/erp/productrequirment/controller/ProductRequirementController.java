package com.aes.erp.productrequirment.controller;

import com.aes.erp.productrequirment.dto.request.ProductRequirementRequestDto;
import com.aes.erp.productrequirment.service.ProductRequirementService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/product-requirements")
public class ProductRequirementController {

    private final ProductRequirementService productRequirementService;

    public ProductRequirementController(ProductRequirementService productRequirementService) {
        this.productRequirementService = productRequirementService;
    }

    @PostMapping
    @ApiOperation(value = "Create Product Requirement ")
    public ResponseEntity<?> addProductRequirement(@RequestBody @Valid ProductRequirementRequestDto productRequirementRequestDto) {
        productRequirementService.createProductRequirement(productRequirementRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    @ApiOperation(value = "Get Product Requirements with Pagination")
    public ResponseEntity<?> getProductRequirement(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("categoryId") Optional<Long> categoryId,
            @RequestParam("subCategoryId") Optional<Long> subCategoryId,
            @RequestParam("startDate") Optional<LocalDateTime> startDate,
            @RequestParam("endDate") Optional<LocalDateTime> endDate
    ) {

        return new ResponseEntity<>(
                productRequirementService.getAllProductRequirements(page, size, categoryId, subCategoryId, startDate, endDate),
                HttpStatus.OK
        );
    }
    @GetMapping("/{categoryId}/{subCategoryId}")
    @ApiOperation(value = "Get Product Requirements View")
    public ResponseEntity<?> getProductRequirementView(
               @PathVariable("categoryId") Optional<Long> categoryId,
               @PathVariable("subCategoryId") Optional<Long> subCategoryId
    ) {

        return new ResponseEntity<>(
                productRequirementService.getAllProductRequirementView(categoryId, subCategoryId),
                HttpStatus.OK
        );
    }
}
