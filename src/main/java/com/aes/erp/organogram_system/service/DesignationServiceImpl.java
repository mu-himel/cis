package com.aes.erp.organogram_system.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DesignationServiceImpl implements DesignationService{

    @Autowired
    private RoleNodeRepository roleNodeRepository;

    @Autowired
    private DepartmentService departmentService;

    @Override
    public List<?> getAllDesignationsByDepartment(Long id, Optional<String> name) {
        Optional<Department> departmentOptional = departmentService.getDepartment(id);
        if(departmentOptional.isEmpty()){
            throw new AesException("Department Not found");
        }
        if(name.isEmpty()){
            return roleNodeRepository.findALlByParentDepartment(departmentOptional.get());
        }
        return roleNodeRepository.findAllByParentDepartmentAndNameLikeIgnoreCase(departmentOptional.get(),
                name.orElse("")+'%');
    }

    @Override
    public Optional<RoleNode> getDesignationById(Long id) {
        return roleNodeRepository.findById(id);
    }

    @Override
    public void createRoleNode() {
        RoleNode roleNode = new RoleNode();
        roleNode.setId(1L);
        roleNode.setName("RootRoleNode");
        roleNodeRepository.save(roleNode);
    }

    @Override
    public void createEnlisterRoleNode() {
        RoleNode roleNode = new RoleNode();
        roleNode.setId(2L);
        roleNode.setName("ENLISTER");
        roleNode.setParentDepartment(new Department(1L));
        roleNodeRepository.save(roleNode);
    }

    @Override
    public void createAuditorRoleNode() {
        RoleNode roleNode = new RoleNode();
        roleNode.setId(3L);
        roleNode.setName("AUDITOR");
        roleNode.setParentDepartment(new Department(1L));
        roleNodeRepository.save(roleNode);
    }

    @Override
    public void createVendorRoleNode() {
        RoleNode roleNode = new RoleNode();
        roleNode.setId(4L);
        roleNode.setName("VENDOR");
        roleNode.setParentDepartment(new Department(1L));
        roleNodeRepository.save(roleNode);
    }

    @Override
    public Optional<RoleNode> findByName(String enlister) {
        return roleNodeRepository.findByName(enlister);
    }
}
