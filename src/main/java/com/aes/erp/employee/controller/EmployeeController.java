package com.aes.erp.employee.controller;

import com.aes.erp.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/next-id")
    public ResponseEntity<?> getNextEmployeeId(){
        Map<String,Object> response = new HashMap<>();
        response.put("code",employeeService.getNextEmployeeId());
        return new ResponseEntity<>(
                response,
              HttpStatus.OK
        );
    }
}
