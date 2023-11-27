package com.aes.erp.module_access.service;

import com.aes.erp.module_access.dto.request.ModuleAccessVerifier;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessVerifierConfig;

import java.util.List;
import java.util.Optional;

public interface ModuleAccessVerifierConfigService {

    void addVerifierConfig(Long modulePermissionId, ModuleAccessVerifier moduleAccessVerifierDto);
    Optional<?> getVerifierConfig(Long modulePermissionId);

    void deleteById(Long id);

    List<ModuleAccessVerifierConfig> getVerifierConfigByModule(ModuleAccess module);
}
