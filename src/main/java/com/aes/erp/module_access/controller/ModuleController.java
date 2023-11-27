package com.aes.erp.module_access.controller;

import com.aes.erp.module_access.entity.ModuleAccessPermission;
import com.aes.erp.module_access.service.ModuleAccessPermissionService;
import com.aes.erp.module_access.service.ModuleAccessService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modules")
public class ModuleController {

    @Autowired
    private ModuleAccessService moduleAccessService;

    @GetMapping
    @ApiOperation(value = "Get All Modules")
    public ResponseEntity<?> getAllModules(){
        return new ResponseEntity<>(
                moduleAccessService.getAllModules(),
                HttpStatus.OK
        );
    }


}
