package com.aes.erp.module_access.service;

import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.repository.ModuleAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuleAccessServiceImpl implements ModuleAccessService{

    @Autowired
    private ModuleAccessRepository moduleAccessRepository;

    @Override
    public List<?> getAllModules() {
        return moduleAccessRepository.findAllOrderByDisplayOrderAsc();
    }

    @Override
    public List<ModuleAccess> getByParentModuleAccess(ModuleAccess moduleAccess) {
        return moduleAccessRepository.findByParentModuleAccess(moduleAccess);
    }

    @Override
    public Optional<ModuleAccess> getModuleAccessByUri(String uri) {
        return moduleAccessRepository.findByUri(uri);
    }
}
