package com.aes.erp.module_access.controller;

import com.aes.erp.module_access.service.ModuleAccessFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/modules/filter")
public class ModuleAccessFilterController {

    @Autowired
    private ModuleAccessFilterService moduleAccessFilterService;

    @DeleteMapping("/{id}")
    public ResponseEntity<?> removeByFilter(
            @PathVariable("id") Long id){
        moduleAccessFilterService.removeFilterById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/{id}/{columnName}")
    public ResponseEntity<?> removeByFilterColumnName(
            @PathVariable("id") Long id,
            @PathVariable("columnName") String columnName){
        moduleAccessFilterService.removeByModulePermissionAndColumnName(id,columnName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
