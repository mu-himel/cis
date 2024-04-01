package com.aes.erp.inventory.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.inventory.service.PendingItemRequestService;

@RestController
@RequestMapping("/api/v1/pending-item-requests")
public class PendingItemReqController {
    
    @Autowired
    private PendingItemRequestService pendingItemRequestService;

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
}
