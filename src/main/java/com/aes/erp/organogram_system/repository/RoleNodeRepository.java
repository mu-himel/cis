package com.aes.erp.organogram_system.repository;

import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleNodeRepository extends JpaRepository<RoleNode, Long> {
    List<RoleNode> findByParentDepartmentId(Long id);
    List<RoleNode> findByParentId(Long id);
    List<RoleNode> findAllByName(String name);

    Optional<RoleNode> findByName(String name);

    List<RoleNodeInfo> findAllByParentDepartmentAndNameLikeIgnoreCase(Department department, String name);

    List<RoleNodeInfo> findALlByParentDepartment(Department department);

    interface RoleNodeInfo{
        Long getId();
        String getName();
    }
}
