package com.aes.erp.module_access.controller;

import com.aes.erp.module_access.service.ModuleAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleController {

    @Autowired
    private ModuleAccessService moduleAccessService;

    @GetMapping
    public ResponseEntity<?> getAllModules(){
        return new ResponseEntity<>(
                moduleAccessService.getAllModules(),
                HttpStatus.OK
        );
    }


}
