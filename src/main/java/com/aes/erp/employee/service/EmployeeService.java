package com.aes.erp.employee.service;

import com.aes.erp.employee.entity.Employee;

import java.util.Optional;

public interface EmployeeService {

    void createEmployee(Employee employee);

    String getNextEmployeeId();

    Optional<Employee> getEmployeeByUserId(Long id);
}
