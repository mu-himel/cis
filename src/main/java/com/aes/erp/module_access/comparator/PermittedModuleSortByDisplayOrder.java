package com.aes.erp.module_access.comparator;

import com.aes.erp.module_access.repository.ModuleAccessPermissionRepository;

public class PermittedModuleSortByDisplayOrder implements java.util.Comparator<com.aes.erp.module_access.repository.ModuleAccessPermissionRepository.PermittedModule> {
    @Override
    public int compare(ModuleAccessPermissionRepository.PermittedModule o1, ModuleAccessPermissionRepository.PermittedModule o2) {
        return o1.getModuleAccess().getDisplayOrder().compareTo(o2.getModuleAccess().getDisplayOrder());
    }
}
