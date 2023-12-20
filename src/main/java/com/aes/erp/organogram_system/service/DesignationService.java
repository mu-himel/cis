package com.aes.erp.organogram_system.service;

import com.aes.erp.organogram_system.entity.RoleNode;

import java.util.List;
import java.util.Optional;

public interface DesignationService {

    List<?> getAllDesignationsByDepartment(Long id, Optional<String> name);

    Optional<RoleNode> getDesignationById(Long id);

    void createRoleNode();
}
