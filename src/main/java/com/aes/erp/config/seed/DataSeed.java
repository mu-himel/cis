package com.aes.erp.config.seed;

import com.aes.erp.module_access.service.ModuleAccessService;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.service.DepartmentService;
import com.aes.erp.organogram_system.service.DesignationService;
import com.aes.erp.organogram_system.user_assignment_system.UserAssignRepository;
import com.aes.erp.organogram_system.user_assignment_system.entity.UserAssignment;
import com.aes.erp.user_management.entity.Role;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.RoleService;
import com.aes.erp.user_management.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class DataSeed implements CommandLineRunner {

    @Autowired
    private RoleService roleService;

    @Autowired
    private DesignationService designationService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserAssignRepository userAssignRepository;

    @Autowired
    private ModuleAccessService moduleAccessService;

    @Override
    public void run(String... args) throws Exception {
        this.init();
    }

    private void init(){
        this.initRoles();
        this.createRoleNode();
        this.createDepartment();
        this.createEnlisterRoleNode();
        this.createAuditorRoleNode();
        this.createVendorRoleNode();
        this.initUserAccounts();
        this.assignUserToRole();
        this.initModules();
    }

    private void initRoles(){
        List<String> roleNames = new ArrayList<>();
        roleNames.add("SYS_ADMIN");
        roleNames.add("VENDOR");
        roleNames.add("ENLISTER");
        roleNames.add("AUDITOR");
        List<Role> roles = roleService.getRoleByRoleNames(roleNames);
        if(roles.size()==0){
            roleService.createRoles(roleNames);
        }
    }

    private void createRoleNode(){
        Optional<RoleNode> roleNode = designationService.getDesignationById(1L);
        if(roleNode.isEmpty()) {
            designationService.createRoleNode();
        }
    }

    private void createDepartment(){
        Optional<Department> department = departmentService.getDepartment(1L);
        if(department.isEmpty()){
            departmentService.createDepartment();
        }
    }

    private void createEnlisterRoleNode(){
        Optional<RoleNode> roleNodeOptional = designationService.findByName("ENLISTER");
        if(roleNodeOptional.isEmpty()){
            designationService.createEnlisterRoleNode();
        }
    }

    private void createAuditorRoleNode(){
        Optional<RoleNode> roleNodeOptional = designationService.findByName("AUDITOR");
        if(roleNodeOptional.isEmpty()){
            designationService.createAuditorRoleNode();
        }
    }

    private void createVendorRoleNode() {
        Optional<RoleNode> roleNodeOptional = designationService.findByName("VENDOR");
        if(roleNodeOptional.isEmpty()){
            designationService.createVendorRoleNode();
        }
    }

    private void initUserAccounts(){
        User user = userService.getUserByUserId(1L);
        if(user==null){
            System.out.println("Super admin Not Exist");
            userService.createSuperAdmin();
        }else{
            System.out.println("Super admin Exist");
        }
    }

    private void assignUserToRole(){
        Optional<UserAssignment> userAssignmentOp = userAssignRepository.findById(1L);
        if(userAssignmentOp.isEmpty()) {
            UserAssignment userAssignment = new UserAssignment();
            userAssignment.setId(1L);
            userAssignment.setUser(new User(1L));
            userAssignRepository.save(userAssignment);
        }
    }

    private void initModules(){
        moduleAccessService.initModuleAccess();
    }
}
