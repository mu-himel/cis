package com.aes.erp.module_access.service;

public interface ModuleAccessFilterService {

    void removeByModulePermissionAndColumnName(Long id, String columnName);

    void removeFilterById(Long id);
}
