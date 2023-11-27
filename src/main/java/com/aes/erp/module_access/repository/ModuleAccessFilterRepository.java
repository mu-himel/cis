package com.aes.erp.module_access.repository;

import com.aes.erp.module_access.entity.ModuleAccessFilter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleAccessFilterRepository extends JpaRepository<ModuleAccessFilter,Long> {


    @Query("SELECT maf FROM ModuleAccessFilter maf " +
            "WHERE maf.moduleAccessPermission.id IN (" +
            "SELECT mp.id FROM ModuleAccessPermission mp " +
            "LEFT JOIN mp.moduleAccess m " +
            "WHERE m.uri=:uri AND mp.department.id = :departmentId " +
            "AND mp.designation IS NULL AND mp.user IS NULL" +
            ")")
    List<ModuleAccessFilter> getFilterByModuleNameAndDepartment(@Param("uri") String uri,
                                               @Param("departmentId") Long departmentId);

    @Query("SELECT maf FROM ModuleAccessFilter maf " +
            "WHERE maf.moduleAccessPermission.id IN (" +
            "SELECT mp.id FROM ModuleAccessPermission mp " +
            "LEFT JOIN mp.moduleAccess m " +
            "WHERE m.uri=:uri AND mp.department.id = :departmentId " +
            "AND mp.designation.id =:designationId AND mp.user IS NULL" +
            ")")
    List<ModuleAccessFilter> getFilterByModuleNameAndDepartmentAndDesignation(
            @Param("uri") String uri,
            @Param("departmentId") Long departmentId,
            @Param("designationId") Long designationId);

    @Query("SELECT maf FROM ModuleAccessFilter maf " +
            "WHERE maf.moduleAccessPermission.id IN (" +
            "SELECT mp.id FROM ModuleAccessPermission mp " +
            "LEFT JOIN mp.moduleAccess m " +
            "WHERE m.uri=:uri AND mp.department.id = :departmentId " +
            "AND mp.designation.id =:designationId AND mp.user.id=:userId" +
            ")")
    List<ModuleAccessFilter> getFilterByModuleNameAndDepartmentAndDesignationAndUser(
            @Param("uri") String uri,
            @Param("departmentId") Long departmentId,
            @Param("designationId") Long designationId,
            @Param("userId") Long userId
    );

    void deleteAllByModuleAccessPermissionIdAndCriteriaColumn(Long id, String columnName);
}
