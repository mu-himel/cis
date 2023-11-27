package com.aes.erp.module_access.repository;

import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.enums.ModuleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleAccessRepository extends JpaRepository<ModuleAccess,Long> {

    @Query("SELECT DISTINCT ma from ModuleAccess ma " +
            "LEFT JOIN FETCH ma.children c " +
            "WHERE ma.parentModuleAccess IS NULL ORDER BY ma.displayOrder ASC")
    List<ModuleAccessInfo> findAllOrderByDisplayOrderAsc();

    List<ModuleAccess> findByParentModuleAccess(ModuleAccess moduleAccess);

    Optional<ModuleAccess> findByUri(String uri);

    interface ModuleAccessInfo{
        Long getId();
        String getName();
        ModuleType getModuleType();
        String getUri();
        String getIcon();
        Integer getDisplayOrder();
        Boolean getShowInMenu();
        List<ChildrenModule> getChildren();
    }

    interface ChildrenModule{
        Long getId();
        String getName();
        ModuleType getModuleType();
        String getUri();
        Integer getDisplayOrder();
        Boolean getShowInMenu();
        ParentModule getParentModuleAccess();
    }
    interface ParentModule{
        Long getId();
        String getName();
    }

}
