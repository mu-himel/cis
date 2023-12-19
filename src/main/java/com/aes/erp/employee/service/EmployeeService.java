package com.aes.erp.employee.service;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.user_management.dto.EmployeeUserDto;

import java.util.Optional;

public interface EmployeeService {

    void createEmployee(Employee employee);

    String getNextEmployeeId();

    Optional<Employee> getEmployeeByUserId(Long id);

    void updateEmployee(Long id, EmployeeUserDto employeeUserDto);
}
