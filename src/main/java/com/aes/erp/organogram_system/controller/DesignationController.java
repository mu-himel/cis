package com.aes.erp.organogram_system.controller;

import com.aes.erp.organogram_system.service.DesignationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/designations")
public class DesignationController {

    @Autowired
    private DesignationService designationService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getDesignations(@PathVariable("id") Long id, @RequestParam("name") Optional<String> name){
        return new ResponseEntity<>(
            designationService.getAllDesignationsByDepartment(id,name),
                HttpStatus.OK
        );
    }
}
