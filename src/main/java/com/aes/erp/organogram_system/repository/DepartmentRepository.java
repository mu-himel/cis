package com.aes.erp.organogram_system.repository;

import com.aes.erp.organogram_system.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Department findByName(String name);

    /*    @Query("select o from Department o where o.parentDepartment = :root")
        List<Department> getChildDept(@Param("root") Department root);*/
    List<Department> findAllByParentDepartmentId(long departmentId);

    List<DepartmentInfo> findAllByNameLikeIgnoreCase(String name);

    @Query("SELECT d FROM Department d")
    List<DepartmentInfo> findAllDepartments();

    interface DepartmentInfo{
        Long getId();
        String getName();
        Integer getLevel();
    }
}
