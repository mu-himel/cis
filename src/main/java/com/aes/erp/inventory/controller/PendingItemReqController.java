package com.aes.erp.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.inventory.dto.request.BulkDeleteDto;
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
        return new ResponseEntity<>(pendingItemRequestService.createPendingItemRequest(pendingItemRequestDto),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePendingItemRequest(@PathVariable("id") Long id){
        pendingItemRequestService.deletePendingItemRequest(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<?> getPendingItemRequests(
        @RequestParam("categoryId") Optional<Long> categoryId,
        @RequestParam("subCategoryId") Optional<Long> subCategoryId,
        @RequestParam("page") Optional<Integer> page,
        @RequestParam("size") Optional<Integer> size

    ){
        return new ResponseEntity<>(
            pendingItemRequestService.getPage(categoryId, subCategoryId,page, size),
            HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailById(@PathVariable("id") Long id){
        return new ResponseEntity<>(
            pendingItemRequestService.getDetail(id).orElse(null),
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

    @DeleteMapping("/pending-brands/{id}")
    public ResponseEntity<?> deletePendingBrand(@PathVariable("id") Long id){
        pendingItemRequestService.deletePendingBrand(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/pending-brands")
    public ResponseEntity<?> deletePendingBrands(@RequestBody BulkDeleteDto bulkDeleteDto){
        pendingItemRequestService.deletePendingBrands(bulkDeleteDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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
    public ResponseEntity<?> savePendingAttribute(@RequestBody PendingAttributesDto pendingAttributesDto){
        pendingItemRequestService.createPendingAttribute(pendingAttributesDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/pending-attributes")
    public ResponseEntity<?> deletePendingAttributes(@RequestBody BulkDeleteDto bulkDeleteDto){
        pendingItemRequestService.deletePendingAttributes(bulkDeleteDto.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/pending-attributes/{id}")
    public ResponseEntity<?> deletePendingAttribute(@PathVariable("id") Long id){
        pendingItemRequestService.deletePendingAttribute(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public record PendingAttributesDto(List<PendingAttributeDto> pendingAttributes){};

    @PutMapping("/reject/{id}")
    public ResponseEntity<?> rejectPendingItem(@PathVariable Long id){
        pendingItemRequestService.rejectPendingItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
