package com.aes.erp.module_access.generic;

import com.aes.erp.module_access.entity.ModuleAccessFilter;

import java.util.List;
import java.util.Optional;

public class MergeFilterPermission<T extends ModuleAccessFilter> {

    public void mergeFilter(List<T> filterPermissions, List<T> filters){

            for(T maf : filterPermissions){
                Optional<T> f = filters.stream().filter(moduleAccessFilter ->
                        moduleAccessFilter.getCriteriaColumn().equals(maf.getCriteriaColumn()) &&
                                moduleAccessFilter.getCriteriaIdValue().equals(maf.getCriteriaIdValue())
                ).findFirst();
                if(f.isEmpty()){
                    filters.add(maf);
                }
            }

    }
}
