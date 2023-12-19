package com.aes.erp.employee.controller;

import com.aes.erp.employee.service.EmployeeService;
import com.aes.erp.user_management.dto.EmployeeUserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable("id") Long id){

        return new ResponseEntity<>(
                employeeService.getEmployeeByUserId(id).orElse(null),
                HttpStatus.OK
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(
            @PathVariable("id") Long id,
            @RequestBody EmployeeUserDto employeeUserDto
    ){
        employeeService.updateEmployee(id,employeeUserDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
