package com.aes.erp.user_management.service;

import com.aes.erp.user_management.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findRoleByRoleName(String roleName);

    @Query(value = "SELECT r FROM Role r " +
            " LEFT JOIN FETCH r.roleToPrivileges rp " +
            " WHERE r.roleName IN (:roleNames)")
    List<Role> findAllByRoleNames(@Param("roleNames") List<String> roleNames);
}
