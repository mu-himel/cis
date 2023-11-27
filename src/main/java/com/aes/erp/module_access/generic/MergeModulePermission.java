package com.aes.erp.module_access.generic;

import com.aes.erp.module_access.repository.ModuleAccessPermissionRepository.PermittedModule;

import java.util.List;
import java.util.Optional;

public class MergeModulePermission<T extends PermittedModule,R extends PermittedModule> {

    public void mergePermission(List<T> permission, List<R> modules) {
        for(T p: permission){
            Optional<?> permittedModuleExist = modules.stream().filter(_p ->
                    _p.getModuleAccess().getName().equals(
                            p.getModuleAccess().getName()
                    )).findFirst();
            if(permittedModuleExist.isEmpty()){
                modules.add((R) p);
            }
        }

    }


}
