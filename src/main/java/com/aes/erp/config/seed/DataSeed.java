package com.aes.erp.config.seed;


import com.aes.erp.inventory.entity.AttributeUnit;
import com.aes.erp.inventory.repository.AttributeUnitRepository;
import com.aes.erp.module_access.service.ModuleAccessPermissionService;
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
import com.aes.erp.vendor.dto.VendorTypeCreateDto;
import com.aes.erp.vendor.service.VendorTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private ModuleAccessPermissionService moduleAccessPermissionService;

    @Autowired
    private VendorTypeService vendorTypeService;

    @Autowired
    private AttributeUnitRepository attributeUnitRepository;

    @Override
    public void run(String... args) throws Exception {
        this.init();
    }

    private void init(){
        this.initRoles();
        this.createDepartment();
        this.createRoleNode();
        this.createEnlisterRoleNode();
        this.createAuditorRoleNode();
        this.createVendorRoleNode();
        this.createInventoryControllerRoleNode();
        this.initUserAccounts();
//
        this.initVendorTypes();
        this.initAttributeUnits();
        this.assignUserToRole();
        this.initModules();

    }

    private void initRoles(){
        List<String> roleNames = new ArrayList<>();
        Role sysAdminRole = roleService.getRoleByRoleName("SYS_ADMIN"); 
        if(sysAdminRole==null){
            roleNames.add("SYS_ADMIN");
        }
        Role vendorRole = roleService.getRoleByRoleName("VENDOR");
        if(vendorRole==null){
            roleNames.add("VENDOR");
        }
        Role employeeRole = roleService.getRoleByRoleName("EMPLOYEE");
        if(employeeRole==null){
            roleNames.add("EMPLOYEE");
        }
        Role enlisterRole = roleService.getRoleByRoleName("ENLISTER");
        if(enlisterRole==null){
            roleNames.add("ENLISTER");
        }
        Role auditorRole = roleService.getRoleByRoleName("AUDITOR");
        if(auditorRole==null){
            roleNames.add("AUDITOR");
        }
        Role orgRole = roleService.getRoleByRoleName("ORGANIZATION");
        if(orgRole==null){
            roleNames.add("ORGANIZATION");
        }
        Role icRole = roleService.getRoleByRoleName("INVENTORY CONTROLLER");
        if(icRole==null){
            roleNames.add("INVENTORY CONTROLLER");
        }
        roleService.createRoles(roleNames);
    }

    @Transactional
    private void createRoleNode(){
        Optional<RoleNode> roleNode = designationService.getDesignationById(1L);
        if(roleNode.isEmpty()) {
            designationService.createRoleNode();
        }
    }

    @Transactional
    private void createDepartment(){
        Optional<Department> department = departmentService.getDepartment(1L);
        if(department.isEmpty()){
            departmentService.createDepartment();
        }
    }

    @Transactional
    private void createEnlisterRoleNode(){
        Optional<RoleNode> roleNodeOptional = designationService.findByName("ENLISTER");
        if(roleNodeOptional.isEmpty()){
            designationService.createEnlisterRoleNode();
        }
    }

    @Transactional
    private void createAuditorRoleNode(){
        Optional<RoleNode> roleNodeOptional = designationService.findByName("AUDITOR");
        if(roleNodeOptional.isEmpty()){
            designationService.createAuditorRoleNode();
        }
    }

    @Transactional
    private void createVendorRoleNode() {
        Optional<RoleNode> roleNodeOptional = designationService.findByName("VENDOR");
        if(roleNodeOptional.isEmpty()){
            designationService.createVendorRoleNode();
        }
    }

    @Transactional
    private void createInventoryControllerRoleNode(){
        Optional<RoleNode> roleNodeOptional = designationService.findByName("INVENTORY CONTROLLER");
        if(roleNodeOptional.isEmpty()){
            designationService.createInventoryControllerRoleNode();
        }
    }

    @Transactional
    private void initUserAccounts(){
        User user = userService.getUserByUserId(1L);
        if(user==null){
            System.out.println("Super admin Not Exist");
            userService.createSuperAdmin(1L, "superadmin@gmail.com", "12345678");
        } else {
            System.out.println("Super admin Exist");
        }

        User admin = userService.getUserByEmail("admin2@gmail.com");
        if (admin == null) {
            userService.createSuperAdmin(2L, "admin2@gmail.com", "12345678");
        }
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void assignUserToRole() {
        Optional<UserAssignment> userAssignmentOp = userAssignRepository.findById(1L);
        if (userAssignmentOp.isEmpty()) {
            UserAssignment userAssignment = new UserAssignment();
            userAssignment.setId(1L);
            userAssignment.setUser(new User(1L));
            userAssignRepository.save(userAssignment);
        }
    }

    private void initModules(){
        moduleAccessService.initModuleAccess();
        moduleAccessPermissionService.initModulePermissions();
    }

    private void initVendorTypes(){
        Long count = vendorTypeService.getVendorTypeCount();
        if(count == 0){
            vendorTypeService.createVendorType(new VendorTypeCreateDto("MANUFACTURER"));
            // vendorTypeService.createVendorType(new VendorTypeCreateDto("SUPPLIER"));
            // vendorTypeService.createVendorType(new VendorTypeCreateDto("GENERAL"));
            // vendorTypeService.createVendorType(new VendorTypeCreateDto("TRADER"));
            vendorTypeService.createVendorType(new VendorTypeCreateDto("DISTRIBUTOR"));
            vendorTypeService.createVendorType(new VendorTypeCreateDto("DEALER"));
            vendorTypeService.createVendorType(new VendorTypeCreateDto("SERVICE PROVIDER"));
            vendorTypeService.createVendorType(new VendorTypeCreateDto("CONTRACTOR"));
        }
    }

    private void initAttributeUnits(){
        Long count = attributeUnitRepository.count();
        if(count == 0){
            List<AttributeUnit> attributeUnits = new ArrayList<>();
            attributeUnits.add(new AttributeUnit(1L, "GB"));
            attributeUnits.add(new AttributeUnit(2L, "Pcs"));
            attributeUnits.add(new AttributeUnit(3L, "Kg"));
            attributeUnits.add(new AttributeUnit(4L, "meter"));
            attributeUnits.add(new AttributeUnit(5L, "cm"));
            attributeUnits.add(new AttributeUnit(6L, "ltr"));
            attributeUnits.add(new AttributeUnit(7L, "gallon"));
            attributeUnits.add(new AttributeUnit(8L, "None"));
            attributeUnitRepository.saveAll(attributeUnits);
        }
    }
}
