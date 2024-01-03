package com.aes.erp.vendor.controller;

import com.aes.erp.vendor.dto.VendorTypeCreateDto;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.service.VendorTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/vendor-types")
public class VendorTypeController {

    @Autowired
    private VendorTypeService vendorTypeService;

    @PostMapping
    public ResponseEntity<?> createVendorType(@RequestBody VendorTypeCreateDto vendorType){
        vendorTypeService.createVendorType(vendorType);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getAllVendorTypes(){
        return new ResponseEntity<>(vendorTypeService.getAlLVendorTypes(),HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> getAllVendorTypes(@RequestParam("page") Optional<Integer> page,
                                               @RequestParam("size") Optional<Integer> size){
        return new ResponseEntity<>(vendorTypeService.getAllVendorTypes(page,size),HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendorType(@PathVariable("id") Long id){
        vendorTypeService.deleteVendorType(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    
}
