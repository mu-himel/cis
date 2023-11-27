package com.aes.erp.module_access.service;

import com.aes.erp.module_access.dto.request.DeletePermissionRequest;
import com.aes.erp.module_access.dto.request.ModuleAccessPermissionRequest;
import com.aes.erp.module_access.dto.request.UpdateModulePermissionRequest;
import com.aes.erp.module_access.enums.ModuleAssignType;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ModuleAccessPermissionService {

    void addModuleAccessPermission(ModuleAccessPermissionRequest moduleAccessPermissionRequest);

    void setModuleAssignType(ModuleAssignType moduleAccessType);

    List<?> getPermissionWiseModules(String token);

    Optional<Map<String,List<Long>>> getModulePermissionFilterByUri(String token, String uri);



    List<?> getPermissionWiseModulesByDepartment(Long id);

    List<?> getPermissionWiseModulesByDesignation(Long departmentId,Long id);

    List<?> getPermissionWiseModulesByUser(Long departmentId,Long designationId, Long id);

    void updateCrudPermission(Long id, UpdateModulePermissionRequest updateModulePermissionRequest);

    void deletePermissionById(Long id, DeletePermissionRequest deletePermissionRequest);

    Optional<?> getModulePermissionByUri(String token, String uri);
}
