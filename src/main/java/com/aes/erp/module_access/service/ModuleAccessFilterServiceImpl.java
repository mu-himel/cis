package com.aes.erp.module_access.service;

import com.aes.erp.module_access.repository.ModuleAccessFilterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ModuleAccessFilterServiceImpl implements ModuleAccessFilterService{

    @Autowired
    private ModuleAccessFilterRepository moduleAccessFilterRepository;

    @Override
    @Transactional
    public void removeByModulePermissionAndColumnName(Long id, String columnName) {
        moduleAccessFilterRepository.deleteAllByModuleAccessPermissionIdAndCriteriaColumn(id,columnName);
    }

    @Override
    @Transactional
    public void removeFilterById(Long id) {
        moduleAccessFilterRepository.deleteById(id);
    }
}
