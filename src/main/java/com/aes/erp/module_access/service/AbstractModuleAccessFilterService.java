package com.aes.erp.module_access.service;

import com.aes.erp.authentication.JwtUtil;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.authentication.dto.EmployeeInfoDto;
import com.aes.erp.module_access.comparator.PermittedModuleSortByDisplayOrder;
import com.aes.erp.module_access.dto.request.ModulePermissionRequest;
import com.aes.erp.module_access.entity.ModuleAccessFilter;
import com.aes.erp.module_access.entity.ModuleAccessPermission;
import com.aes.erp.module_access.entity.ModuleAccessVerifierConfig;
import com.aes.erp.module_access.repository.ModuleAccessFilterRepository;
import com.aes.erp.module_access.repository.ModuleAccessPermissionRepository;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.service.DepartmentService;
import com.aes.erp.organogram_system.service.DesignationService;
import com.aes.erp.user_management.service.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class AbstractModuleAccessFilterService {

    @Autowired
    protected ModuleAccessPermissionRepository moduleAccessPermissionRepository;

    @Autowired
    protected ModuleAccessFilterRepository moduleAccessFilterRepository;

    @Autowired
    protected ModuleAccessService moduleAccessService;

    @Autowired
    protected DepartmentService departmentService;

    @Autowired
    protected DesignationService designationService;

    @Autowired
    protected JwtUtil jwtUtil;



    protected List<ModuleAccessPermissionRepository.PermittedModule> _getPermittedModules(ClaimResponseDto claimResponseDto){
        List<String> roles = claimResponseDto.getAuthorities().stream()
                .map(dto->dto.getAuthority())
                .collect(Collectors.toList());
        RoleNode roleNode = null;
        System.out.println(roles);
        String employeeType = (String)claimResponseDto.getUserInfoDto().get("employeeType");
        if(employeeType!=null && (employeeType.contains("EMPLOYEE")||employeeType.contains("AUDITOR")||employeeType.contains("ENLISTER"))){
            System.out.println();
            Optional<RoleNode> roleNodeOptional = designationService.findByName(employeeType);
            if(roleNodeOptional.isPresent()){
                roleNode = roleNodeOptional.get();
            }
        }
        else if(employeeType!=null && employeeType.contains("INVENTORY_CONTROLLER")){
            Optional<RoleNode> roleNodeOptional = designationService.findByName(employeeType.toString().replace("_"," "));
            if(roleNodeOptional.isPresent()){
                roleNode = roleNodeOptional.get();
            }
        }else if(roles.contains("ROLE_VENDOR")){
            Optional<RoleNode> roleNodeOptional = designationService.findByName("VENDOR");
            if(roleNodeOptional.isPresent()){
                roleNode = roleNodeOptional.get();
            }
        }
        List<ModuleAccessPermissionRepository.PermittedModule> permittedModules = new ArrayList<>();
//        if(roles.contains("ROLE_EMPLOYEE")) {
            Map<String,Object> employeeInfoDto = claimResponseDto.getUserInfoDto();
//            List<ModuleAccessPermissionRepository.PermittedModule> userPermittedModules =
//                    moduleAccessPermissionRepository.findAllByDepartmentIdAndDesignationIdAndUserId(
////                            (Long)employeeInfoDto.get("departmentId"),
////                            (Long)employeeInfoDto.get("designationId"),
//                             1L,null,
//                            null
//                    );

            List<ModuleAccessPermissionRepository.PermittedModule> rolePermittedRoleModules =
                    moduleAccessPermissionRepository
                            .findAllByDepartmentIdAndDesignationId(
                                    1L,
                                    roleNode.getId()
                            );

            List<ModuleAccessPermissionRepository.PermittedModule> departmentPermittedModules =
                    moduleAccessPermissionRepository
                            .findAllByDepartmentId((Long)employeeInfoDto.get("departmentId"));


//            for (ModuleAccessPermissionRepository.PermittedModule p : userPermittedModules) {
//                Optional<?> permittedModuleExist = permittedModules.stream().filter(permittedModule ->
//                        permittedModule.getModuleAccess().getName().equals(
//                                p.getModuleAccess().getName()
//                        )).findFirst();
//                if (permittedModuleExist.isEmpty()) {
//                    permittedModules.add(p);
//                }
//            }

            for (ModuleAccessPermissionRepository.PermittedModule p : rolePermittedRoleModules) {
                Optional<?> permittedModuleExist = permittedModules.stream().filter(permittedModule ->
                        permittedModule.getModuleAccess().getName().equals(
                                p.getModuleAccess().getName()
                        )).findFirst();
                if (permittedModuleExist.isEmpty()) {
                    permittedModules.add(p);
                }
            }

            for (ModuleAccessPermissionRepository.PermittedModule p : departmentPermittedModules) {
                Optional<?> permittedModuleExist = permittedModules.stream().filter(permittedModule ->
                        permittedModule.getModuleAccess().getName().equals(
                                p.getModuleAccess().getName()
                        )).findFirst();
                if (permittedModuleExist.isEmpty()) {
                    permittedModules.add(p);
                }
            }
//        }


        permittedModules.sort(new PermittedModuleSortByDisplayOrder());
        return permittedModules;
    }

    protected List<ModuleAccessFilter> _getModuleFilters(ModuleAccessPermission moduleAccessPermission,
                                                       ModulePermissionRequest mpr){
        List<ModuleAccessFilter> filters=new ArrayList<>();
        mpr.getFilters().stream().forEach((mf)->{
            mf.getColumnValues().stream().forEach((fcv)->{
                ModuleAccessFilter maf = new ModuleAccessFilter();
                maf.setModuleAccessPermission(moduleAccessPermission);
                String[] columnNameSegments = mf.getColumnName().split("_");

                maf.setCriteriaGroup(((columnNameSegments.length>0)?
                        columnNameSegments[0].toUpperCase():""));

                maf.setCriteriaColumn(mf.getColumnName());
                maf.setCriteriaIdValue(fcv.getId());
                maf.setCriteriaText(fcv.getName());
                filters.add(maf);
            });
        });
        return filters;
    }

    protected List<ModuleAccessVerifierConfig> _getVerifiers(ModulePermissionRequest mp, ModuleAccessPermission moduleAccessPermission) {
        return mp.getVerifiers().stream()
                .map(moduleAccessVerifier -> {
                    ModuleAccessVerifierConfig config = new ModuleAccessVerifierConfig();
                    config.setModuleAccessPermission(moduleAccessPermission);
                    config.setModuleAccess(moduleAccessPermission.getModuleAccess());
                    config.setLevel(moduleAccessVerifier.getLevel());
                    config.setCriteriaColumn(moduleAccessVerifier.getCriteriaColumn());
                    config.setCriteriaGroup(moduleAccessVerifier.getCriteriaGroup());
                    config.setCriteriaIdValue(moduleAccessVerifier.getCriteriaIdValue());
                    config.setCriteriaText(moduleAccessVerifier.getCriteriaText());
                    return config;
                }).collect(Collectors.toList());
    }
}
