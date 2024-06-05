package com.aes.erp.inventory.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.inventory.dto.request.bulk_gen.BulkGenConfigDto;
import com.aes.erp.inventory.service.BulkItemGenConfigService;

@RestController
@RequestMapping("/api/v1/bulk-item-gen-config")
public class BulkGenController {

    @Autowired
    private BulkItemGenConfigService bulkItemGenConfigService;

    @PostMapping
    public ResponseEntity<?> saveBulkGenerationConfig(@RequestBody BulkGenConfigDto BulkGenConfigDto){
        Map<String,Object> data = new HashMap<>();
        data.put("id",bulkItemGenConfigService.saveConfig(BulkGenConfigDto));
        return new ResponseEntity<>(data,HttpStatus.CREATED);
    }

    
    
}
