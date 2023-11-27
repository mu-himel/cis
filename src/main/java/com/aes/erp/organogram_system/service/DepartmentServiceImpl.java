package com.aes.erp.organogram_system.service;

import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentServiceImpl implements DepartmentService{

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public List<?> getAllDepartments(Optional<String> name) {
        if(name.isEmpty()){
            return departmentRepository.findAllDepartments();
        }
        return departmentRepository.findAllByNameLikeIgnoreCase(name.get()+'%');
    }

    @Override
    public Optional<Department> getDepartment(Long id) {
        return departmentRepository.findById(id);
    }
}
