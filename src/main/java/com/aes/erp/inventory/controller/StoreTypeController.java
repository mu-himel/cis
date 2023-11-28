package com.aes.erp.inventory.controller;

import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;
import com.aes.erp.inventory.service.StoreTypeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/store_types")
public class StoreTypeController {
    private final StoreTypeService storeTypeService;

    public StoreTypeController(StoreTypeService storeTypeService) {
        this.storeTypeService = storeTypeService;
    }

    @PostMapping
    @ApiOperation(value = "Create a store type")
    public ResponseEntity<StoreTypeGetDto> addStoreType(@RequestBody @Valid StoreTypeCreateDto createDto){
        return new ResponseEntity<StoreTypeGetDto>(storeTypeService.createStoreType(createDto), HttpStatus.CREATED);
    }
}
