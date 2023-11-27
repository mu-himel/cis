package com.aes.erp.module_access.repository;

import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessFilter;
import com.aes.erp.module_access.entity.ModuleAccessPermission;
import com.aes.erp.module_access.entity.ModuleAccessVerifierConfig;
import com.aes.erp.module_access.enums.ModuleType;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleAccessPermissionRepository extends JpaRepository<ModuleAccessPermission,Long> {

    @Query(value = "SELECT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "WHERE d.id = :departmentId AND mp.designation IS NULL AND mp.user IS NULL")
    List<PermittedModule> findAllByDepartmentId(@Param("departmentId") Long departmentId);


    @Query(value = "SELECT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "WHERE d.id = :departmentId AND mp.designation.id = :designationId AND mp.user IS NULL")
    List<PermittedModule> findAllByDepartmentIdAndDesignationId(@Param("departmentId") Long departmentId,
                                                                @Param("designationId") Long designationId);

    @Query(value = "SELECT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "WHERE d.id = :departmentId AND mp.designation.id = :designationId AND mp.user.id = :userId")
    List<PermittedModule> findAllByDepartmentIdAndDesignationIdAndUserId(@Param("departmentId") Long departmentId,
                                                                         @Param("designationId") Long designationId,
                                                                         @Param("userId") Long userId);

    Integer countAllByModuleAccessAndDepartmentAndDesignationIsNullAndUserIsNull(ModuleAccess moduleAccess, Department department);

    Integer countAllByModuleAccessAndDepartmentAndDesignationAndUserIsNull(ModuleAccess moduleAccess, Department department, RoleNode roleNode);

    Integer countAllByModuleAccessAndDepartmentAndDesignationAndUser(ModuleAccess moduleAccess, Department department, RoleNode roleNode, User user);

    @Query(value = "SELECT mp from ModuleAccessPermission mp " +
            "WHERE mp.moduleAccess.id=:moduleAccessId " +
            "AND (:departmentId IS NULL OR mp.department.id=:departmentId) " +
            "AND (:designationId IS NULL AND mp.designation.id IS NULL OR mp.designation.id=:designationId) " +
            " AND (:userId IS NULL AND mp.user.id IS NULL OR mp.user.id=:userId)")
    Optional<ModuleAccessPermission> findAllByDepartmentIdAndModuleAccessIdAndDesignationIdAndUserId(
            @Param("moduleAccessId") Long moduleAccessId,
            @Param("departmentId") Long departmentId,
            @Param("designationId") Long designationId,
            @Param("userId") Long userId);

//    Optional<ModuleAccessPermission> findAllByDepartmentIdAndDesignationIdAndUserIdAndModuleAccessId(
//            Long departmentId,
//            Long designationId,
//            Long userId,
//            Long parentId);
//
//    Optional<ModuleAccessPermission> findAllByDepartmentIdAndDesignationIdAndModuleAccessId(
//            Long departmentId,
//            Long designationId,Long parentId);

    @Query(value = "SELECT DISTINCT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "LEFT JOIN FETCH mp.filters f " +
            "WHERE d.id = :departmentId AND mp.designation IS NULL AND mp.user IS NULL")
    List<PermittedModuleWithFilter> findAllByDepartmentIdWithFilter(@Param("departmentId") Long departmentId);

    @Query(value = "SELECT DISTINCT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "LEFT JOIN FETCH mp.filters f " +
            "WHERE mp.designation.id=:designationId AND mp.user IS NULL")
    List<PermittedModuleWithFilter> findAllByDesignationId(@Param("designationId") Long designationId);

    @Query(value = "SELECT DISTINCT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "LEFT JOIN FETCH mp.filters f " +
            "WHERE mp.user.id=:userId")
    List<PermittedModuleWithFilter> findAllByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "LEFT JOIN FETCH mp.verifiers v " +
            "WHERE ma.uri = :uri AND d.id=:departmentId")
    List<PermittedModuleWithVerifier> findAllByModuleUriAndDepartment(
            @Param("uri") String uri,
            @Param("departmentId") Long departmentId
            );

    @Query(value = "SELECT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "LEFT JOIN FETCH mp.designation ds " +
            "LEFT JOIN FETCH mp.verifiers v " +
            "WHERE ma.uri = :uri AND d.id=:departmentId AND ds.id=:designationId")
    List<PermittedModuleWithVerifier> findAllByModuleUriAndDepartmentAndDesignation(
            @Param("uri") String uri,
            @Param("departmentId") Long departmentId,
            @Param("designationId") Long designationId
    );

    @Query(value = "SELECT mp FROM ModuleAccessPermission mp " +
            "LEFT JOIN FETCH mp.moduleAccess ma " +
            "LEFT JOIN FETCH mp.department d " +
            "LEFT JOIN FETCH mp.designation ds " +
            "LEFT JOIN FETCH mp.user u " +
            "LEFT JOIN FETCH mp.verifiers v " +
            "WHERE ma.uri = :uri AND d.id=:departmentId AND ds.id=:designationId AND u.id=:userId")
    List<PermittedModuleWithVerifier> findAllByModuleUriAndDepartmentAndDesignationAndUser(
            @Param("uri") String uri,
            @Param("departmentId") Long departmentId,
            @Param("designationId") Long designationId,
            @Param("userId") Long userId
    );

//    void deleteAllByModuleAccessIdExists(List<Long> childIds);

  

    void deleteAllByDepartmentIdAndDesignationIdAndUserIdAndModuleAccessIdIn(
            Long departmentId,
            Long designationId,
            Long userId,
            List<Long> childIds);

    void deleteAllByDepartmentIdAndDesignationIdAndUserIdIsNullAndModuleAccessIdIn(Long departmentId, Long designationId, List<Long> childIds);

    void deleteAllByDepartmentIdAndDesignationIdIsNullAndUserIdIsNullAndModuleAccessIdIn(Long departmentId, List<Long> childIds);

    interface PermittedModule{
        Long getId();
        DepartmentInfo getDepartment();
        ObjectInfo getDesignation();

        UserInfo getUser();

        ModuleInfo getModuleAccess();

        Boolean getCreatePermission();
        Boolean getReadPermission();
        Boolean getUpdatePermission();
        Boolean getDeletePermission();
    }

    interface UserInfo {
        Long getId();

        String getFirstName();

        String getLastName();
    }

    interface PermittedModuleWithFilter extends PermittedModule{
        List<ModuleAccessFilter> getFilters();
    }

    interface PermittedModuleWithVerifier extends PermittedModule{
        List<ModuleAccessVerifierConfig> getVerifiers();
    }

    interface DepartmentInfo extends ObjectInfo{
        Integer getLevel();
    }

    interface ObjectInfo{
        Long getId();
        String getName();
    }

    interface ModuleInfo extends ObjectInfo{
        ModuleType getModuleType();
        ObjectInfo getParentModuleAccess();

        Integer getDisplayOrder();
        Boolean getShowInMenu();
        String getUri();
        String getIcon();
        String getRoute();
    }
}
