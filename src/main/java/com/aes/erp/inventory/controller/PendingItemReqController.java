package com.aes.erp.inventory.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.inventory.dto.request.PendingAttributeDto;
import com.aes.erp.inventory.dto.request.PendingBrandDto;
import com.aes.erp.inventory.dto.request.PendingItemRequestDto;
import com.aes.erp.inventory.service.PendingItemRequestService;

@RestController
@RequestMapping("/api/v1/pending-item-requests")
public class PendingItemReqController {
    
    @Autowired
    private PendingItemRequestService pendingItemRequestService;

    @PostMapping
    public ResponseEntity<?> createPendingItemRequest(@RequestBody PendingItemRequestDto pendingItemRequestDto){
        pendingItemRequestService.createPendingItemRequest(pendingItemRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<?> getPendingItemReqeusts(
        @RequestParam("page") Optional<Integer> page,
        @RequestParam("size") Optional<Integer> size 
    ){
        return new ResponseEntity<>(
            pendingItemRequestService.getPage(page, size),
            HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailById(@PathVariable("id") Long id){
        return new ResponseEntity<>(
            pendingItemRequestService.getDetail(id),
            HttpStatus.OK
        );
    }

    @GetMapping("/pending-brands/{subCatId}")
    public ResponseEntity<?> getPendingBrands(@PathVariable("subCatId") Long subCatId){
        return new ResponseEntity<>(
            pendingItemRequestService.getPendingBrands(subCatId),
            HttpStatus.OK
        );
    }

    @PostMapping("/pending-brands")
    public ResponseEntity<?> savePendingBrand(@RequestBody PendingBrandDto pendingBrandDto){
        pendingItemRequestService.createPendingBrand(pendingBrandDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/pending-attributes/{subCatId}")
    public ResponseEntity<?> getPendingAttributes(@PathVariable("subCatId") Long subCatId){
        return new ResponseEntity<>(
            pendingItemRequestService.getPendingAttributes(subCatId),
            HttpStatus.OK
        );
    }

    @PostMapping("/pending-attributes")
    public ResponseEntity<?> savePendingAttribute(@RequestBody PendingAttributeDto pendingAttributeDto){
        pendingItemRequestService.createPendingAttribute(pendingAttributeDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
