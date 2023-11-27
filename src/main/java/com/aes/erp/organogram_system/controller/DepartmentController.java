package com.aes.erp.organogram_system.controller;

import com.aes.erp.organogram_system.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<?> getDepartments(@RequestParam("name") Optional<String> name){

        return new ResponseEntity<>(
                departmentService.getAllDepartments(name),
                HttpStatus.OK
        );
    }
}
