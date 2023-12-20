package com.aes.erp.module_access.service;

import com.aes.erp.module_access.entity.ModuleAccess;

import java.util.List;
import java.util.Optional;

public interface ModuleAccessService {

    List<?> getAllModules();

    List<ModuleAccess> getByParentModuleAccess(ModuleAccess moduleAccess);

    Optional<ModuleAccess> getModuleAccessByUri(String uri);

    void initModuleAccess();
}
