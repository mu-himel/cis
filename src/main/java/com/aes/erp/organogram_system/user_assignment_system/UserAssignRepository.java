package com.aes.erp.organogram_system.user_assignment_system;

import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.user_assignment_system.entity.UserAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAssignRepository extends JpaRepository<UserAssignment, Long> {
    UserAssignment save(UserAssignment userAssignment);
    UserAssignment findByUserIdAndDepartmentIdAndRoleNodeId(long userId, long departmentId, long roleId);
    List<UserAssignment> findByDepartmentId(long departmentId);
    List<UserAssignment> findByRoleNodeId(long roleId);

    List<UserAssignment> findAllByUserId(long userId);
    @Query(value = "SELECT u.id from user u, user_assignment ua, role_node r where ua.role_node_id = r.id and ua.user_id = u.id and r.id = ?1 ", nativeQuery = true)
    List<Long> findAllUserIdsByRoleNodeId(long roleId);
    UserAssignment findTopByOrderByIdDesc();


    @Query(value = "SELECT u.id, concat(u.first_name,' ',u.last_name) as name, e.employee_id as employeeId, e.id as empId " +
            "FROM user_assignment ua " +
            "LEFT JOIN users u on u.id = ua.user_id " +
            "LEFT JOIN employees e on e.user_id = u.id " +
            "WHERE ua.department_id=:departmentId AND ua.role_node_id=:roleNodeId",nativeQuery = true)
    List<UserAssignmentInfo> findAllByDepartmentAndRoleNode(Long departmentId, Long roleNodeId);

    interface UserAssignmentInfo{
        Long getId();
        String getName();
        String getEmployeeId();
        Long getEmpId();
    }


}
