package com.aes.erp.organogram_system.user_assignment_system;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.employee.service.EmployeeService;
import com.aes.erp.exception.AesException;
import com.aes.erp.exception.ExceptionMessage;
import com.aes.erp.organogram_system.dto.FSReturnObject;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.repository.DepartmentRepository;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import com.aes.erp.organogram_system.user_assignment_system.dto.UserAssignmentDTO;
import com.aes.erp.organogram_system.user_assignment_system.dto.UserOrganogramData;
import com.aes.erp.organogram_system.user_assignment_system.entity.UserAssignment;
import com.aes.erp.user_management.dto.UserResponseDTO;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAssignmentOps {

    private final UserAssignRepository userAssignRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleNodeRepository roleNodeRepository;
    private final UserRepository userRepository;

    private final EmployeeService employeeService;

    public static int count = 0;

    @Transactional
    public UserAssignment assignUser(UserAssignmentDTO userAssignDTO) {
        count++;

        Optional<User> userOptional = userRepository.findById(userAssignDTO.getUserId());
        if(userOptional.isEmpty()) {
            throw new AesException(ExceptionMessage.USER_NOT_FOUND);
        }

        Optional<Department> departmentOptional = departmentRepository.findById(userAssignDTO.getDepartmentId());
        if(departmentOptional.isEmpty()){
            throw new AesException(ExceptionMessage.DEPARTMENT_DOES_NOT_EXIST);
        }

        RoleNode roleNode = roleNodeRepository.findById(userAssignDTO.getRoleId()).orElse(null);
        if(Objects.isNull(roleNode))
            throw new AesException("RoleNode doesn't exist");

        UserAssignment parentUserAssignment =
                (userAssignDTO.getParentUserId() == null)
                        ? null
                        : userAssignRepository.findById(userAssignDTO.getParentUserId()).orElse(null);

        // assign user
        Department department = departmentOptional.get();
        department.setHasUser(true);
        UserAssignment userAssignment = new UserAssignment();
        userAssignment.setUser(userOptional.get());
        userAssignment.setDepartment(department);
        userAssignment.setRoleNode(roleNode);
        userAssignment.setParentUserAssignment(parentUserAssignment);

        userAssignment = userAssignRepository.save(userAssignment);

        Optional<Employee> employeeOptional = employeeService.getEmployeeByUserId(userAssignDTO.getUserId());
        if(employeeOptional.isPresent()){
            Employee employee = employeeOptional.get();
            Employee parentUserEmployeeProfile = null;

            if(parentUserAssignment!=null && parentUserAssignment.getUser()!=null) {
                Long reportingManagerId = parentUserAssignment.getUser().getId();
                Optional<Employee> parentUserOptional = employeeService.getEmployeeByUserId(reportingManagerId);
                if (parentUserOptional.isPresent()) {
                    parentUserEmployeeProfile = parentUserOptional.get();
                }
            }

            employee.setUser(new User(userAssignDTO.getUserId()));
            employee.setReportingManager(parentUserEmployeeProfile);
            employee.setDepartment(new Department(userAssignDTO.getDepartmentId()));
            employee.setRoleNode(new RoleNode(userAssignDTO.getRoleId()));
        }

        return userAssignment;
    }

    //Update Only For RoleNode
    public FSReturnObject updateAssignedUser(long assignedUserId, UserAssignmentDTO userAssignDTO) {
        UserAssignment assignedUser = userAssignRepository.findById(assignedUserId).orElse(null);

        if(Objects.isNull(assignedUser)) return new FSReturnObject().setReturnObject(false, "No assigned user found", null);

        User user = userRepository.findById(userAssignDTO.getUserId()).orElse(null);
        if(Objects.isNull(user)) return new FSReturnObject().setReturnObject(false, "User doesn't exist", null);

        Department department = departmentRepository.findById(userAssignDTO.getDepartmentId()).orElse(null);
        if(Objects.isNull(department)) return new FSReturnObject().setReturnObject(false, "Department doesn't exist", null);

        RoleNode roleNode = roleNodeRepository.findById(userAssignDTO.getRoleId()).orElse(null);
        if(Objects.isNull(roleNode)) return new FSReturnObject().setReturnObject(false, "RoleNode doesn't exist", null);

        UserAssignment parentUser = userAssignRepository.findById(userAssignDTO.getParentUserId()).orElse(null);
        if(Objects.isNull(parentUser)) return new FSReturnObject().setReturnObject(false, "Invalid parent user", null);

        assignedUser.setUser(user);
        assignedUser.setDepartment(department);
        assignedUser.setRoleNode(roleNode);
        assignedUser.setParentUserAssignment(parentUser);
        return new FSReturnObject().setReturnObject(true, "Assigned user successfully updated", userAssignRepository.save(assignedUser));
    }

    public FSReturnObject deleteAssignedUser(long userAssignmentId) {
        UserAssignment assignedUser = userAssignRepository.findById(userAssignmentId).orElse(null);
        if(Objects.isNull(assignedUser)) return new FSReturnObject().setReturnObject(false, "No assigned user found", null);
        Department department = assignedUser.getDepartment();

        /*assignedUser.setParentUserAssignment(null);
        assignedUser.setDepartment(null);
        assignedUser.setRoleNode(null);
        assignedUser.setUser(null);
        userAssignRepository.save(assignedUser);*/

        userAssignRepository.delete(assignedUser);

        //if(userAssignRepository.findByDepartmentId(department.getId()).size() <= 1) {
        if(userAssignRepository.findByDepartmentId(department.getId()).size() == 0) {
            department.setHasUser(false);
            departmentRepository.save(department);
        }

        return new FSReturnObject().setReturnObject(true, "Assigned user successfully removed", assignedUser);
    }

    public List<UserOrganogramData> getUserAssignmentTree(long departmentId) {
        List<UserAssignment> userAssignList = userAssignRepository.findByDepartmentId(departmentId);
        List<UserOrganogramData> result = new ArrayList<>();
        for(UserAssignment userAssignment : userAssignList) {
            UserOrganogramData userOrganogramData = new UserOrganogramData();
            userOrganogramData.setUser(userAssignment);
            userOrganogramData.setParentUser(userAssignment.getParentUserAssignment());
            userOrganogramData.setChilds(userAssignment.getChilds());
            /*userOrganogramData.setUserId(user.getUser().getId());
            if(user.getParentUser() != null)
                userOrganogramData.setParentId(user.getParentUser().getUser().getId());
            userOrganogramData.setChilds(user.getChilds().stream()
                            .map(u -> u.getUser().getId())
                            .collect(Collectors.toList()));
            userOrganogramData.setRoleId(user.getRoleNode().getId());*/
            result.add(userOrganogramData);
        }

        return result;
//        return new FSReturnObject().setReturnObject(true,"assigned user successfully fetched", result);
    }

    public void deleteAssignedUserByDepartment(Department department) {
        try {
            List<UserAssignment> userAssignments = userAssignRepository.findByDepartmentId(department.getId());
            for (UserAssignment userAssignment : userAssignments) {
                if (userAssignment.getParentUserAssignment() == null) {
                    userAssignRepository.delete(userAssignment); // deleting rootUserAssignment will delete children also
                }
            }
//            return new FSReturnObject().setReturnObject(true, "Successful", null);
        } catch (Exception e) {
            e.printStackTrace();
              throw new AesException("Department removal failed: Assigned user can not be deleted");
//            return new FSReturnObject().setReturnObject(false, e.getMessage(), null);
        }
    }

    public void deleteAssignedUserByRoleNode(RoleNode roleNode) {
        try {
            List<UserAssignment> userAssignments = userAssignRepository.findByRoleNodeId(roleNode.getId());
            for (UserAssignment userAssignment : userAssignments) {
                deleteAssignedUser(userAssignment);
            }
//            return new FSReturnObject().setReturnObject(true, "Successful", null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException("Role Node removal failed: Assigned user can not be deleted");
//            return new FSReturnObject().setReturnObject(false, e.getMessage(), null);
        }
    }

    private void deleteAssignedUser(UserAssignment userAssignment) {
        userAssignment.setParentUserAssignment(null);
        userAssignment.setDepartment(null);
        userAssignment.setRoleNode(null);
        userAssignment.setUser(null);
        userAssignRepository.save(userAssignment);
        userAssignRepository.delete(userAssignment);
    }

    public List<RoleNode> getRoleNodesByUserId(long userId) {
        List<UserAssignment> assignedRoleObjects = userAssignRepository.findAllByUserId(userId);
        List<RoleNode> roleNodes = assignedRoleObjects.stream().map(assignedRoleObject -> assignedRoleObject.getRoleNode()).collect(Collectors.toList());
        return roleNodes;
    }

    public FSReturnObject getUsersPerRoleNode(long roleNodeId) {
        List<Long> userIds = userAssignRepository.findAllUserIdsByRoleNodeId(roleNodeId);
        if(userIds.isEmpty() || userIds == null) {
            return new FSReturnObject().setReturnObject(true, "users does not exist", null);
        }
        List<UserResponseDTO> userResponses = userIds.stream().map(userId -> UserResponseDTO.getUserResponseDTO(userRepository.findById(userId).orElse(null))).collect(Collectors.toList());
        return new FSReturnObject().setReturnObject(true, "users are fetched", userResponses);

    }

    public FSReturnObject getLastIndexedUser() {
        return new FSReturnObject().setReturnObject(true, "last indexed id is fetched", count);
    }

    public List<?> getManagersByDepartmentAndRoleNode(Long departmentId, Long designationId) {
        Optional<Department> departmentOptional = departmentRepository.findById(departmentId);
        Optional<RoleNode> roleNodeOptional = roleNodeRepository.findById(designationId);
        if(departmentOptional.isEmpty()){
            throw new AesException("Department not found");
        }
        if(roleNodeOptional.isEmpty()){
            throw new AesException("Designation not found");
        }
        return userAssignRepository
                .findAllByDepartmentAndRoleNode(departmentOptional.get().getId(),
                        roleNodeOptional.get().getId());
    }

    List<?> getUnassignedUsers(){
        return userRepository.findAllUnassignedUsers();
    }
}
