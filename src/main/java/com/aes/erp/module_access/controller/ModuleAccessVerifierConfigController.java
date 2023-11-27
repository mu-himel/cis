package com.aes.erp.module_access.controller;

import com.aes.erp.module_access.dto.request.ModuleAccessVerifier;
import com.aes.erp.module_access.service.ModuleAccessVerifierConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/verifier-configs")
public class ModuleAccessVerifierConfigController {

    @Autowired
    private ModuleAccessVerifierConfigService verifierConfigService;

    @PutMapping("/{modulePermissionId}")
    public ResponseEntity<?> addVerifierConfig(
            @PathVariable("modulePermissionId") Long modulePermissionId,
            @RequestBody ModuleAccessVerifier verifier){
        verifierConfigService.addVerifierConfig(modulePermissionId,verifier);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{modulePermissionId}")
    public ResponseEntity<?> getModuleAccessVerifierConfig(@PathVariable("modulePermissionId") Long modulePermissionId){
        return new ResponseEntity<>(
            verifierConfigService.getVerifierConfig(modulePermissionId),
            HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeModuleAccessVerifierConfig(@PathVariable("id") Long id){
        verifierConfigService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
