package com.aes.erp.inventory.controller;

import com.aes.erp.inventory.dto.request.StoreTypeCreateDto;
import com.aes.erp.inventory.dto.response.StoreTypeGetDto;
import com.aes.erp.inventory.service.StoreTypeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/store_types")
public class StoreTypeController {
    private final StoreTypeService storeTypeService;

    public StoreTypeController(StoreTypeService storeTypeService) {
        this.storeTypeService = storeTypeService;
    }

    @PostMapping
    @ApiOperation(value = "Create a store type")
    public ResponseEntity<?> addStoreType(@RequestBody @Valid StoreTypeCreateDto createDto){
        storeTypeService.createStoreType(createDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    @ApiOperation(value = "Get A Store Type By Id")
    public ResponseEntity<?> getAllStoreTypes(@PathVariable("id") Long id){
        return new ResponseEntity<>(storeTypeService.getById(id), HttpStatus.OK);
    }
    @GetMapping
    @ApiOperation(value = "Get All Store Types")
    public ResponseEntity<?> getAllStoreTypes(@RequestParam("searchFilter") Optional<String> searchFilter,
                                                @RequestParam("page") Optional<Integer> page,
                                              @RequestParam("size") Optional<Integer> size){
        return new ResponseEntity<>(storeTypeService.getAllStoreTypes(searchFilter, page, size), HttpStatus.OK);
    }
    @PutMapping("/{id}")
    @ApiOperation(value = "Update a Store Type")
    public ResponseEntity<?> updateStoreType(@PathVariable("id") Long id, @RequestBody @Valid StoreTypeCreateDto dto){
        storeTypeService.updateStoreType(id, dto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete a Store Type")
    public ResponseEntity<?> updateStoreType(@PathVariable("id") Long id){
        storeTypeService.deleteStoreType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
