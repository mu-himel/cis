package com.aes.erp.user_management.service;

import com.aes.erp.user_management.entity.RoleToPrivilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoleToPrivilegeRepository extends JpaRepository<RoleToPrivilege, Long> {
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM role_to_privilege WHERE role_id=?1 AND privilege_id=?2", nativeQuery = true)
    void deleteRoleToPrivilegeByRoleIdAndPrivilegeId(long roleId, long privilegeId);
  
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM role_to_privilege WHERE role_id=?1", nativeQuery = true)
    void deleteAllByRoleId(long id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM role_to_privilege WHERE privilege_id=?1", nativeQuery = true)
    void deleteAllByPrivilegeId(long id);

    List<RoleToPrivilege> findAllByRoleId(long id);

    List<RoleToPrivilege> findAllByRoleIdAndPrivilegeId(long roleId, long privilegeId);

}
