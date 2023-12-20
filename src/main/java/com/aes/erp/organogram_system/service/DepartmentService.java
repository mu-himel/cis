package com.aes.erp.organogram_system.service;

import com.aes.erp.organogram_system.entity.Department;

import java.util.List;
import java.util.Optional;

public interface DepartmentService {

    List<?> getAllDepartments(Optional<String> name);

    Optional<Department> getDepartment(Long id);

    void createDepartment();
}
