package com.aes.erp.module_access.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.authentication.dto.EmployeeInfoDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.module_access.comparator.PermittedModuleSortByDisplayOrder;
import com.aes.erp.module_access.dto.request.DeletePermissionRequest;
import com.aes.erp.module_access.dto.request.ModuleAccessPermissionRequest;
import com.aes.erp.module_access.dto.request.ModulePermissionRequest;
import com.aes.erp.module_access.dto.request.UpdateModulePermissionRequest;
import com.aes.erp.module_access.dto.response.ModuleAccessResponse;
import com.aes.erp.module_access.dto.response.ParentModuleAccessResponse;
import com.aes.erp.module_access.dto.response.SubModuleAccessResponse;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessFilter;
import com.aes.erp.module_access.entity.ModuleAccessPermission;
import com.aes.erp.module_access.enums.ModuleAssignType;
import com.aes.erp.module_access.enums.ModuleType;
import com.aes.erp.module_access.generic.MergeFilterPermission;
import com.aes.erp.module_access.generic.MergeModulePermission;
import com.aes.erp.module_access.repository.ModuleAccessPermissionRepository.*;
import com.aes.erp.module_access.repository.ModuleAccessRepository;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.user_management.entity.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModuleAccessPermissionServiceImpl extends AbstractModuleAccessFilterService
                                               implements ModuleAccessPermissionService{


    private ModuleAssignType moduleAssignType;

    private Department department;

    private RoleNode roleNode;

    private User user;



    @Override
    public void addModuleAccessPermission(
            ModuleAccessPermissionRequest moduleAccessPermissionRequest) {

        ModuleAccess moduleAccess = new ModuleAccess();
        moduleAccess.setId(moduleAccessPermissionRequest.getId());
        if(moduleAccessPermissionRequest.getModuleType()== ModuleType.PARENT){
            // parent module permission assigning
            this._savePermission(moduleAccess,moduleAccessPermissionRequest,ModuleType.PARENT);

            List<ModuleAccess> moduleAccesses = moduleAccessService
                                    .getByParentModuleAccess(moduleAccess);
            moduleAccesses.stream().forEach(module->{
                // child module permission assigning
                this._savePermission(module,moduleAccessPermissionRequest,ModuleType.CHILD);
            });

        } else {
            // if request for child module permission assigning
            this._savePermission(moduleAccess,moduleAccessPermissionRequest,ModuleType.CHILD);
        }
        this.department=null;
        this.roleNode=null;
        this.user=null;
    }

    @Override
    public void setModuleAssignType(ModuleAssignType moduleAccessType) {
        this.moduleAssignType = moduleAccessType;
    }

    @Override
    public List<?> getPermissionWiseModules(String token) {
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        List<String> roles = claimResponseDto.getAuthorities().stream()
                .map(dto->dto.getAuthority())
                .collect(Collectors.toList());

        if(roles.contains("ROLE_SYS_ADMIN")){
            return moduleAccessService.getAllModules();
        }
//        else if(roles.contains("ROLE_VENDOR")){
//            List<ModuleAccessRepository.ModuleAccessInfo> allModules = (List<ModuleAccessRepository.ModuleAccessInfo>) moduleAccessService.getAllModules();
//            List<ModuleAccessRepository.ModuleAccessInfo> vendorModules = new ArrayList<>();
//            for (ModuleAccessRepository.ModuleAccessInfo module : allModules) {
//                if(module.getUri().startsWith("vendor-panel")){
//                    vendorModules.add(module);
//                }
//            }
//            return vendorModules;
//        }
        else{

            List<PermittedModule> permittedModules = _getPermittedModules(claimResponseDto);

            List<ModuleAccessResponse> modules = new ArrayList<>();
            permittedModules.stream().forEach(permittedModule -> {
                if(permittedModule.getModuleAccess().getModuleType()== ModuleType.PARENT) {
                    ModuleAccessResponse moduleAccess = new ModuleAccessResponse();
                    moduleAccess.setModuleType(ModuleType.PARENT);
                    moduleAccess.setId(permittedModule.getModuleAccess().getId());
                    moduleAccess.setName(permittedModule.getModuleAccess().getName());
                    moduleAccess.setShowInMenu(permittedModule.getModuleAccess().getShowInMenu());
                    moduleAccess.setDisplayOrder(permittedModule.getModuleAccess().getDisplayOrder());
                    moduleAccess.setUri(permittedModule.getModuleAccess().getUri());
                    moduleAccess.setIcon(permittedModule.getModuleAccess().getIcon());
                    moduleAccess.setRoute(permittedModule.getModuleAccess().getRoute());
                    moduleAccess.setCreatePermission(permittedModule.getCreatePermission());
                    moduleAccess.setReadPermission(permittedModule.getReadPermission());
                    moduleAccess.setUpdatePermission(permittedModule.getUpdatePermission());
                    moduleAccess.setDeletePermission(permittedModule.getDeletePermission());
                    modules.add(moduleAccess);
                }
            });
            permittedModules.stream().forEach(permittedModule -> {
                if(permittedModule.getModuleAccess().getModuleType()== ModuleType.CHILD) {
                    Optional<ModuleAccessResponse> moduleAccessOptional = modules.stream().filter(m->{
                        return m.getId().equals(permittedModule.getModuleAccess().getParentModuleAccess().getId());
                    }).findFirst();
                    if(moduleAccessOptional.isPresent()) {
                        ModuleAccessResponse parentModule = moduleAccessOptional.get();
                        SubModuleAccessResponse subModuleAccessResponse = new SubModuleAccessResponse();
                        subModuleAccessResponse.setModuleType(ModuleType.CHILD);
                        subModuleAccessResponse.setId(permittedModule.getModuleAccess().getId());
                        subModuleAccessResponse.setName(permittedModule.getModuleAccess().getName());
                        subModuleAccessResponse.setShowInMenu(permittedModule.getModuleAccess().getShowInMenu());
                        subModuleAccessResponse.setDisplayOrder(permittedModule.getModuleAccess().getDisplayOrder());
                        subModuleAccessResponse.setUri(permittedModule.getModuleAccess().getUri());
                        subModuleAccessResponse.setIcon(permittedModule.getModuleAccess().getIcon());
                        subModuleAccessResponse.setRoute(permittedModule.getModuleAccess().getRoute());

                        subModuleAccessResponse.setCreatePermission(permittedModule.getCreatePermission());
                        subModuleAccessResponse.setReadPermission(permittedModule.getReadPermission());
                        subModuleAccessResponse.setUpdatePermission(permittedModule.getUpdatePermission());
                        subModuleAccessResponse.setDeletePermission(permittedModule.getDeletePermission());

                        ParentModuleAccessResponse parentModuleAccessResponse = new ParentModuleAccessResponse();
                        parentModuleAccessResponse.setId(permittedModule.getModuleAccess()
                                .getParentModuleAccess().getId());
                        parentModuleAccessResponse.setName(permittedModule.getModuleAccess()
                                .getParentModuleAccess().getName());
                        subModuleAccessResponse.setParentModuleAccess(parentModuleAccessResponse);
                        parentModule.getChildren().add(subModuleAccessResponse);

                    }
                }
            });
            return modules;
        }

    }




    @Override
    public Optional<Map<String,List<Long>>> getModulePermissionFilterByUri(String token, String uri) {
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        Map<String,List<Long>> filters = new HashMap<>();
        List<ModuleAccessFilter> filterByModuleNameAndDepartmentAndDesignationAndUser=new ArrayList<>();
        List<ModuleAccessFilter> filterByModuleNameAndDepartmentAndDesignation=new ArrayList<>();
        List<ModuleAccessFilter> filterByModuleNameAndDepartment = new ArrayList<>();
        if(claimResponseDto.getUserInfoDto()!=null) {
            // some code pending
            EmployeeInfoDto employeeInfoDto = (EmployeeInfoDto)claimResponseDto.getUserInfoDto();
            filterByModuleNameAndDepartmentAndDesignationAndUser = moduleAccessFilterRepository
                    .getFilterByModuleNameAndDepartmentAndDesignationAndUser(
                            uri,
                            employeeInfoDto.getDepartmentId(),
                            employeeInfoDto.getDesignationId(),
                            employeeInfoDto.getId()
                    );
            filterByModuleNameAndDepartmentAndDesignation = moduleAccessFilterRepository
                    .getFilterByModuleNameAndDepartmentAndDesignation(uri, employeeInfoDto.getDepartmentId(),
                            employeeInfoDto.getDesignationId());

            filterByModuleNameAndDepartment = moduleAccessFilterRepository
                    .getFilterByModuleNameAndDepartment(uri, employeeInfoDto.getDepartmentId());

        }
            List<ModuleAccessFilter> permittedFilters = new ArrayList<>();

        MergeFilterPermission<ModuleAccessFilter> mergeFilterPermission = new MergeFilterPermission<>();
        mergeFilterPermission.mergeFilter(filterByModuleNameAndDepartmentAndDesignationAndUser, permittedFilters);
        mergeFilterPermission.mergeFilter(filterByModuleNameAndDepartmentAndDesignation, permittedFilters);
        mergeFilterPermission.mergeFilter(filterByModuleNameAndDepartment, permittedFilters);

        permittedFilters.stream().forEach(moduleAccessFilter -> {
            if(filters.containsKey(moduleAccessFilter.getCriteriaColumn())){
                filters.get(moduleAccessFilter.getCriteriaColumn())
                        .add(Long.parseLong(moduleAccessFilter.getCriteriaIdValue()));
            }else{
                List<Long> values = new ArrayList<>();
                Long idValue = Long.parseLong(moduleAccessFilter.getCriteriaIdValue());
                values.add(idValue);
                filters.put(moduleAccessFilter.getCriteriaColumn(),values);
            }
        });

        return (filters.size()>0)? Optional.ofNullable(filters) : Optional.empty();
    }


    @Override
    public List<?> getPermissionWiseModulesByDepartment(Long departmentId) {

        List<PermittedModuleWithFilter> allByDepartmentIdWithFilter =

                        moduleAccessPermissionRepository.findAllByDepartmentIdWithFilter(departmentId);
        allByDepartmentIdWithFilter.sort(new PermittedModuleSortByDisplayOrder());
        return allByDepartmentIdWithFilter;
    }

    @Override
    public List<?> getPermissionWiseModulesByDesignation(Long departmentId,Long designationId) {

        List<PermittedModuleWithFilter> designationPermittedModuleWithFilters =
                moduleAccessPermissionRepository.findAllByDesignationId(designationId);

        List<PermittedModuleWithFilter> departmentPermittedModuleWithFilters=
                moduleAccessPermissionRepository
                        .findAllByDepartmentIdWithFilter(departmentId);

        List<PermittedModuleWithFilter> modules = new ArrayList<>();

        MergeModulePermission<PermittedModuleWithFilter,PermittedModuleWithFilter> mergeModulePermission = new MergeModulePermission<>();
        mergeModulePermission.mergePermission(designationPermittedModuleWithFilters, modules);
        mergeModulePermission.mergePermission(departmentPermittedModuleWithFilters, modules);

        modules.sort(new PermittedModuleSortByDisplayOrder());
        return modules;
    }


    @Override
    public List<?> getPermissionWiseModulesByUser(Long departmentId, Long designationId, Long userId) {

        List<PermittedModuleWithFilter> userPermittedModuleWithFilters =
                moduleAccessPermissionRepository.findAllByUserId(userId);

        List<PermittedModuleWithFilter> designationPermittedModuleWithFilters =
                moduleAccessPermissionRepository.findAllByDesignationId(designationId);

        List<PermittedModuleWithFilter> departmentPermittedModuleWithFilters=
                moduleAccessPermissionRepository.findAllByDepartmentIdWithFilter(departmentId);

        List<PermittedModuleWithFilter> modules = new ArrayList<>();

        MergeModulePermission<PermittedModuleWithFilter,PermittedModuleWithFilter> mergeModulePermission = new MergeModulePermission<>();
        mergeModulePermission.mergePermission(userPermittedModuleWithFilters, modules);
        mergeModulePermission.mergePermission(designationPermittedModuleWithFilters, modules);
        mergeModulePermission.mergePermission(departmentPermittedModuleWithFilters, modules);

        modules.sort(new PermittedModuleSortByDisplayOrder());
        return modules;
    }

    @Override
    public void updateCrudPermission(Long id, UpdateModulePermissionRequest updateModulePermissionRequest) {
        Optional<ModuleAccessPermission> moduleAccessPermissionOp = moduleAccessPermissionRepository.findById(id);
        if(moduleAccessPermissionOp.isPresent()){
            ModuleAccessPermission moduleAccessPermission = moduleAccessPermissionOp.get();
            Department department = moduleAccessPermission.getDepartment();
            RoleNode designation = moduleAccessPermission.getDesignation();
            User user = moduleAccessPermission.getUser();
            if((department !=null && designation == null && user == null &&
                updateModulePermissionRequest.getDepartmentId()!=null &&
                    updateModulePermissionRequest.getDesignationId() == null &&
                    updateModulePermissionRequest.getUserId() == null) || (
                    department !=null && designation != null && user == null &&
                            updateModulePermissionRequest.getDepartmentId()!=null &&
                            updateModulePermissionRequest.getDesignationId() != null &&
                            updateModulePermissionRequest.getUserId() == null
                    ) || (
                        department !=null && designation != null && user != null &&
                                updateModulePermissionRequest.getDepartmentId()!=null &&
                                updateModulePermissionRequest.getDesignationId() != null &&
                                updateModulePermissionRequest.getUserId() != null
                    )
            ) {
                moduleAccessPermission.setCreatePermission(updateModulePermissionRequest.getCreatePermission());
                moduleAccessPermission.setUpdatePermission(updateModulePermissionRequest.getUpdatePermission());
                moduleAccessPermission.setReadPermission(updateModulePermissionRequest.getReadPermission());
                moduleAccessPermission.setDeletePermission(updateModulePermissionRequest.getDeletePermission());

                if(updateModulePermissionRequest.getFilters().size()>0) {
                    moduleAccessPermission.setFilters(updateModulePermissionRequest
                            .getFilters().stream().map(moduleAccessFilter -> {
                                moduleAccessFilter.setModuleAccessPermission(moduleAccessPermission);
                                return moduleAccessFilter;
                            }).collect(Collectors.toList()));
                }

                moduleAccessPermissionRepository.save(moduleAccessPermission);
            }

            if( department !=null && designation == null && user == null &&
                    updateModulePermissionRequest.getDepartmentId()!=null &&
                    updateModulePermissionRequest.getDesignationId() != null &&
                    updateModulePermissionRequest.getUserId() == null

            ) {
                ModuleAccessPermission moduleAccessPermission1 = new ModuleAccessPermission();
                moduleAccessPermission1.setDepartment(department);

                if(updateModulePermissionRequest.getDesignationId() != null){
                    roleNode = new RoleNode(updateModulePermissionRequest.getDesignationId());
                }

                if(updateModulePermissionRequest.getUserId() != null){
                    user = new User(updateModulePermissionRequest.getUserId());
                }
                moduleAccessPermission1.setDesignation(roleNode);
                moduleAccessPermission1.setUser(user);
                moduleAccessPermission1.setModuleAccess(moduleAccessPermission.getModuleAccess());
                moduleAccessPermission1.setCreatePermission(updateModulePermissionRequest.getCreatePermission());
                moduleAccessPermission1.setUpdatePermission(updateModulePermissionRequest.getUpdatePermission());
                moduleAccessPermission1.setReadPermission(updateModulePermissionRequest.getReadPermission());
                moduleAccessPermission1.setDeletePermission(updateModulePermissionRequest.getDeletePermission());

                moduleAccessPermission1.setFilters(updateModulePermissionRequest
                        .getFilters().stream().map(moduleAccessFilter -> {
                    moduleAccessFilter.setModuleAccessPermission(moduleAccessPermission1);
                    return moduleAccessFilter;
                }).collect(Collectors.toList()));
                moduleAccessPermissionRepository.save(moduleAccessPermission1);
            }

            if( (
                    (department !=null && designation == null && user == null) ||
                    (department !=null && designation != null && user == null )
                ) &&
                    updateModulePermissionRequest.getDepartmentId()!=null &&
                    updateModulePermissionRequest.getDesignationId() != null &&
                    updateModulePermissionRequest.getUserId() != null

            ){
                ModuleAccessPermission moduleAccessPermission1 = new ModuleAccessPermission();
                moduleAccessPermission1.setDepartment(department);

                if(updateModulePermissionRequest.getDesignationId() != null){
                    roleNode = new RoleNode(updateModulePermissionRequest.getDesignationId());
                }

                if(updateModulePermissionRequest.getUserId() != null){
                    user = new User(updateModulePermissionRequest.getUserId());
                }
                moduleAccessPermission1.setDesignation(roleNode);
                moduleAccessPermission1.setUser(user);
                moduleAccessPermission1.setModuleAccess(moduleAccessPermission.getModuleAccess());
                moduleAccessPermission1.setCreatePermission(updateModulePermissionRequest.getCreatePermission());
                moduleAccessPermission1.setUpdatePermission(updateModulePermissionRequest.getUpdatePermission());
                moduleAccessPermission1.setReadPermission(updateModulePermissionRequest.getReadPermission());
                moduleAccessPermission1.setDeletePermission(updateModulePermissionRequest.getDeletePermission());
                moduleAccessPermission1.setFilters(updateModulePermissionRequest
                        .getFilters().stream().map(moduleAccessFilter -> {
                    moduleAccessFilter.setModuleAccessPermission(moduleAccessPermission1);
                    return moduleAccessFilter;
                }).collect(Collectors.toList()));
                moduleAccessPermissionRepository.save(moduleAccessPermission1);
            }
        }
    }

    @Override
    @Transactional
    public void deletePermissionById(Long id, DeletePermissionRequest deletePermissionRequest) {
        Optional<ModuleAccessPermission> moduleAccessPermissionOp = moduleAccessPermissionRepository.findById(id);
        if(moduleAccessPermissionOp.isPresent()){
            ModuleAccessPermission moduleAccessPermission = moduleAccessPermissionOp.get();
            List<ModuleAccess> childModules = moduleAccessPermission.getModuleAccess().getChildren();
            List<Long> childIds = childModules.stream().map(childModule-> childModule.getId())
                    .collect(Collectors.toList());
            if(deletePermissionRequest.getDepartmentId()!=null && deletePermissionRequest.getDesignationId()!=null
            && deletePermissionRequest.getUserId()!=null) {
                moduleAccessPermissionRepository
                        .deleteAllByDepartmentIdAndDesignationIdAndUserIdAndModuleAccessIdIn(
                                deletePermissionRequest.getDepartmentId(),
                                deletePermissionRequest.getDesignationId(),
                                deletePermissionRequest.getUserId(),
                                childIds);
            }
            if(deletePermissionRequest.getDepartmentId()!=null && deletePermissionRequest.getDesignationId()!=null
                    && deletePermissionRequest.getUserId()==null) {
                moduleAccessPermissionRepository
                        .deleteAllByDepartmentIdAndDesignationIdAndUserIdIsNullAndModuleAccessIdIn(
                                deletePermissionRequest.getDepartmentId(),
                                deletePermissionRequest.getDesignationId(),
                                childIds);
            }
            if(deletePermissionRequest.getDepartmentId()!=null && deletePermissionRequest.getDesignationId()==null
                    && deletePermissionRequest.getUserId()==null) {
                moduleAccessPermissionRepository
                        .deleteAllByDepartmentIdAndDesignationIdIsNullAndUserIdIsNullAndModuleAccessIdIn(
                                deletePermissionRequest.getDepartmentId(),
                                childIds);
            }
        }
        moduleAccessPermissionRepository.deleteById(id);
    }


    @Override
    public Optional<?> getModulePermissionByUri(String token, String uri) {
        ClaimResponseDto claimResponseDto = jwtUtil.extractId(token);
        List<PermittedModuleWithVerifier> modules = new ArrayList<>();
        List<String> roles = claimResponseDto.getAuthorities().stream()
                .map(dto->dto.getAuthority())
                .collect(Collectors.toList());
        if(roles.contains("ROLE_EMPLOYEE")) {
            EmployeeInfoDto employeeInfoDto = (EmployeeInfoDto) claimResponseDto.getUserInfoDto();
            List<PermittedModuleWithVerifier> userWise =
                    moduleAccessPermissionRepository.findAllByModuleUriAndDepartmentAndDesignationAndUser(
                            uri, employeeInfoDto.getDepartmentId(),
                            employeeInfoDto.getDesignationId(),
                            claimResponseDto.getId()
                    );
            List<PermittedModuleWithVerifier> designationWise =
                    moduleAccessPermissionRepository.findAllByModuleUriAndDepartmentAndDesignation(
                            uri, employeeInfoDto.getDepartmentId(),
                            employeeInfoDto.getDesignationId()
                    );
            List<PermittedModuleWithVerifier> departmentWise =
                    moduleAccessPermissionRepository.findAllByModuleUriAndDepartment(
                            uri, employeeInfoDto.getDepartmentId()
                    );

            MergeModulePermission<PermittedModuleWithVerifier,
                    PermittedModuleWithVerifier> mergePermitterVerifer = new MergeModulePermission<>();
            mergePermitterVerifer.mergePermission(userWise, modules);
            mergePermitterVerifer.mergePermission(designationWise, modules);
            mergePermitterVerifer.mergePermission(departmentWise, modules);
            modules.sort(new PermittedModuleSortByDisplayOrder());
        }
        return (modules.size()>0)? Optional.ofNullable(modules.get(0)) : Optional.empty();
    }


    private void _saveParentPermission(ModuleAccessPermissionRequest moduleAccessPermissionRequest,
                                       ModulePermissionRequest mp,
                                       ModuleType moduleType){

        if(moduleType == ModuleType.CHILD) {
//            moduleAccessPermissionRequest.getParentId();
            Optional<ModuleAccessPermission> parentModulePermissionOp =
                    isModuleAccessPermissionExist(moduleAccessPermissionRequest.getParentId(),
                            this.department.getId(),
                            (this.roleNode!=null)? this.roleNode.getId() : null,
                            (this.user!=null)? this.user.getId() : null);

            if(parentModulePermissionOp.isEmpty() && moduleAccessPermissionRequest.getParentId()!=null){
                ModuleAccessPermission parentModuleAccessPermission = new ModuleAccessPermission();
                parentModuleAccessPermission.setModuleAccess(new ModuleAccess(moduleAccessPermissionRequest.getParentId()));
                parentModuleAccessPermission.setCreatePermission(mp.getPermission().getCreatePermission());
                parentModuleAccessPermission.setReadPermission(mp.getPermission().getReadPermission());
                parentModuleAccessPermission.setUpdatePermission(mp.getPermission().getUpdatePermission());
                parentModuleAccessPermission.setDeletePermission(mp.getPermission().getDeletePermission());
                parentModuleAccessPermission.setFilters(_getModuleFilters(parentModuleAccessPermission, mp));
                if(mp.getVerifiers()!=null) {
                    parentModuleAccessPermission.setVerifiers(_getVerifiers(mp, parentModuleAccessPermission));
                }
                this._setModuleAssignTo(parentModuleAccessPermission, mp);
                moduleAccessPermissionRepository.save(parentModuleAccessPermission);
            }
        }
    }

    private Optional<ModuleAccessPermission> isModuleAccessPermissionExist(
            Long moduleId,Long departmentId, Long designationId, Long userId
    ) {
        Optional<ModuleAccessPermission> parentModulePermissionOp = Optional.empty();
        if (this.department != null && this.roleNode != null && this.user != null) {

            parentModulePermissionOp = moduleAccessPermissionRepository
                    .findAllByDepartmentIdAndModuleAccessIdAndDesignationIdAndUserId
                            (moduleId, departmentId, designationId,userId);


        }
        if (this.department != null && this.roleNode != null && this.user == null) {

            parentModulePermissionOp = moduleAccessPermissionRepository
                    .findAllByDepartmentIdAndModuleAccessIdAndDesignationIdAndUserId
                            (moduleId,departmentId,designationId, null);


        }
        if (this.department != null && this.roleNode == null && this.user == null) {

            parentModulePermissionOp = moduleAccessPermissionRepository
                    .findAllByDepartmentIdAndModuleAccessIdAndDesignationIdAndUserId
                            (
                                    moduleId,
                                    this.department.getId(),
                                    null,null);


        }
        return parentModulePermissionOp;
    }

    private void _savePermission(ModuleAccess moduleAccess,
                                 ModuleAccessPermissionRequest moduleAccessPermissionRequest,
                                 ModuleType moduleType){
        List<ModuleAccessPermission> moduleAccessPermissions = new ArrayList<>();
                moduleAccessPermissionRequest
                .getModulePermissions().stream().forEach(mp -> {

                    ModuleAccessPermission moduleAccessPermission = new ModuleAccessPermission();
                    moduleAccessPermission.setModuleAccess(moduleAccess);

                    this._setModuleAssignTo(moduleAccessPermission, mp);

                    this._saveParentPermission(moduleAccessPermissionRequest,mp,moduleType);

                    Optional<ModuleAccessPermission> parentModulePermissionOp
                            = isModuleAccessPermissionExist(moduleAccessPermissionRequest.getId(),
                            this.department.getId(),
                            (this.roleNode !=null)? this.roleNode.getId():null,
                            (this.user!=null)? this.user.getId() : null

                    );

                    if(parentModulePermissionOp.isEmpty() && moduleType==ModuleType.PARENT) {
                        moduleAccessPermission.setCreatePermission(mp.getPermission().getCreatePermission());
                        moduleAccessPermission.setReadPermission(mp.getPermission().getReadPermission());
                        moduleAccessPermission.setUpdatePermission(mp.getPermission().getUpdatePermission());
                        moduleAccessPermission.setDeletePermission(mp.getPermission().getDeletePermission());
                        moduleAccessPermission.setFilters(_getModuleFilters(moduleAccessPermission, mp));
                        if (mp.getVerifiers() != null) {
                            moduleAccessPermission.setVerifiers(_getVerifiers(mp, moduleAccessPermission));
                        }
                        moduleAccessPermissions.add(moduleAccessPermission);
                    }else if(moduleType==ModuleType.CHILD){
                        Optional<ModuleAccessPermission> childModulePermissionOp
                                = isModuleAccessPermissionExist(moduleAccess.getId(),
                                this.department.getId(),
                                (this.roleNode !=null)? this.roleNode.getId():null,
                                (this.user!=null)? this.user.getId() : null

                        );
                        if(childModulePermissionOp.isEmpty()) {
                            moduleAccessPermission.setCreatePermission(mp.getPermission().getCreatePermission());
                            moduleAccessPermission.setReadPermission(mp.getPermission().getReadPermission());
                            moduleAccessPermission.setUpdatePermission(mp.getPermission().getUpdatePermission());
                            moduleAccessPermission.setDeletePermission(mp.getPermission().getDeletePermission());
                            moduleAccessPermission.setFilters(_getModuleFilters(moduleAccessPermission, mp));
                            if (mp.getVerifiers() != null) {
                                moduleAccessPermission.setVerifiers(_getVerifiers(mp, moduleAccessPermission));
                            }
                            moduleAccessPermissions.add(moduleAccessPermission);
                        }
                    }

                });

        if(moduleAccessPermissions.size()>0) {
            moduleAccessPermissionRepository.saveAll(moduleAccessPermissions);
        }
    }



    private void _setModuleAssignTo(ModuleAccessPermission moduleAccessPermission,
                                    ModulePermissionRequest mp){
        if(moduleAssignType==ModuleAssignType.DEPARTMENT) {
            this._setDepartment(moduleAccessPermission, mp);
        }
        if(moduleAssignType==ModuleAssignType.ROLE) {
            this._setDepartment(moduleAccessPermission, mp);
            this._setRoleNode(moduleAccessPermission, mp);

        }
        if(moduleAssignType==ModuleAssignType.USER) {
            this._setUser(moduleAccessPermission, mp);
            this._setDepartment(moduleAccessPermission, mp);
            this._setRoleNode(moduleAccessPermission, mp);
        }
        moduleAccessPermission.setDepartment(this.department);
        moduleAccessPermission.setDesignation(this.roleNode);
        moduleAccessPermission.setUser(this.user);
    }

    private void _checkAndSetDepartment(ModulePermissionRequest mp){
        if(this.department==null || !this.department.getId().equals(mp.getDepartmentId())){
            Optional<Department> departmentOptional = departmentService.getDepartment(mp.getDepartmentId());
            if(departmentOptional.isEmpty()){
                throw new AesException("Department Missing");
            }
            this.department = departmentOptional.get();
        }

    }
    private void _setDepartment(ModuleAccessPermission moduleAccessPermission, ModulePermissionRequest mp){
        if(moduleAssignType!=null && moduleAssignType==ModuleAssignType.DEPARTMENT) {
            this._checkAndSetDepartment(mp);

//            Integer isExist = moduleAccessPermissionRepository
//                .countAllByModuleAccessAndDepartmentAndDesignationIsNullAndUserIsNull(
//                        moduleAccessPermission.getModuleAccess(),
//                        this.department
//                );
//
//            if(isExist>0){
//                throw new AesException("This module permission already exist on the Department");
//            }

        }
    }

    private void _checkAndSetDesignation(ModulePermissionRequest mp){
        if(this.roleNode == null || !this.roleNode.getId().equals(mp.getDesignationId())){
            Optional<RoleNode> designationOptional = designationService.getDesignationById(mp.getDesignationId());
            if(designationOptional.isEmpty()){
                throw new AesException("Role/Designation Missing");
            }
            this.roleNode = designationOptional.get();
        }
    }

    private void _setRoleNode(ModuleAccessPermission moduleAccessPermission, ModulePermissionRequest mp){
        if(moduleAssignType!=null && moduleAssignType==ModuleAssignType.ROLE){

            this._checkAndSetDesignation(mp);
            this._checkAndSetDepartment(mp);

            Integer isExist = moduleAccessPermissionRepository
                    .countAllByModuleAccessAndDepartmentAndDesignationAndUserIsNull(
                            moduleAccessPermission.getModuleAccess(),
                            this.department,
                            this.roleNode
                    );

            if(isExist>0){
                throw new AesException("This module permission already exist on the Designation");
            }

        }
    }



    private void _setUser(ModuleAccessPermission moduleAccessPermission, ModulePermissionRequest mp){
        if(moduleAssignType!=null && moduleAssignType==ModuleAssignType.USER){
            if(this.user == null){
                this.user = new User(mp.getUserId());
            }
            this._checkAndSetDesignation(mp);
            this._checkAndSetDepartment(mp);

            Integer isExist = moduleAccessPermissionRepository
                    .countAllByModuleAccessAndDepartmentAndDesignationAndUser(
                            moduleAccessPermission.getModuleAccess(),
                            this.department,
                            this.roleNode,
                            this.user
                    );

            if(isExist>0){
                throw new AesException("This module permission already exist on the User");
            }
        }
    }

    @Override
    public void initModulePermissions() {
        // ADD PERMISSION FOR VENDOR ROLE_NODE
        addPermission(1L,4L,2L,true);

        // ADD PERMISSION FOR ENLISTER ROLE_NODE
        addPermission(1L,2L,11L,true);
        addPermission(1L,2L,12L,true);
        addPermission(1L,2L,13L,true);
        addPermission(1L,2L,14L,false);
        addPermission(1L,2L,15L,true);
        addPermission(1L,2L,3L,true);
        addPermission(1L,2L,15L,true);

        // ADD PERMISSION FOR AUDITOR ROLE_NODE
        addPermission(1L,3L,11L,true);
        addPermission(1L,3L,12L,true);
        addPermission(1L,3L,13L,false);
        addPermission(1L,3L,14L,true);
        addPermission(1L,3L,9L,true);

    }

    private void addPermission(Long departmentId, Long roleNodeId, Long moduleId,Boolean permission){
        ModuleAccessPermission mapDashboard = new ModuleAccessPermission();
        mapDashboard.setDepartment(new Department(departmentId));
        mapDashboard.setDesignation(new RoleNode(roleNodeId));
        mapDashboard.setModuleAccess(new ModuleAccess(moduleId));
        mapDashboard.setCreatePermission(permission);
        mapDashboard.setReadPermission(permission);
        mapDashboard.setUpdatePermission(permission);
        mapDashboard.setDeletePermission(permission);
        moduleAccessPermissionRepository.save(mapDashboard);
    }
}

