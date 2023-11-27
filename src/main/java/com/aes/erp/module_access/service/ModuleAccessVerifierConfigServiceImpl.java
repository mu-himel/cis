package com.aes.erp.module_access.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.module_access.dto.request.ModuleAccessVerifier;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessPermission;
import com.aes.erp.module_access.entity.ModuleAccessVerifierConfig;
import com.aes.erp.module_access.repository.ModuleAccessPermissionRepository;
import com.aes.erp.module_access.repository.ModuleAccessVerifierConfigRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleAccessVerifierConfigServiceImpl implements ModuleAccessVerifierConfigService {

    @Autowired
    private ModuleAccessVerifierConfigRepository moduleAccessVerifierConfigRepository;

    @Autowired
    private ModuleAccessPermissionRepository moduleAccessPermissionServiceRepository;

    @Override
    @Transactional
    public void addVerifierConfig(Long modulePermissionId, ModuleAccessVerifier moduleAccessVerifierDto) {

        Optional<ModuleAccessPermission> moduleAccessPermissionOp = moduleAccessPermissionServiceRepository.findById(modulePermissionId);
        if(moduleAccessPermissionOp.isEmpty()){
            throw new AesException("Module Permission not found");
        }

        ModuleAccessPermission moduleAccessPermission = moduleAccessPermissionOp.get();
        ModuleAccessVerifierConfig moduleAccessVerifierConfig = new ModuleAccessVerifierConfig();
        BeanUtils.copyProperties(moduleAccessVerifierDto,moduleAccessVerifierConfig);
        moduleAccessVerifierConfig.setModuleAccessPermission(moduleAccessPermission);

        List<ModuleAccessVerifierConfig> verifierConfigs = moduleAccessPermission.getVerifiers();
        verifierConfigs.add(moduleAccessVerifierConfig);

        moduleAccessPermission.setVerifiers(verifierConfigs);

    }

    @Override
    public Optional<?> getVerifierConfig(Long modulePermissionId) {
        return moduleAccessVerifierConfigRepository.findAllByModuleAccessPermissionId(modulePermissionId);
    }

    @Override
    public void deleteById(Long id) {
        moduleAccessVerifierConfigRepository.deleteById(id);
    }

    @Override
    public List<ModuleAccessVerifierConfig> getVerifierConfigByModule(ModuleAccess module) {
        return moduleAccessVerifierConfigRepository.findByModule(module);
    }


}
