package com.aes.erp.organogram_system;

import com.aes.erp.exception.AesException;
import com.aes.erp.helper.ConsoleHelper;
import com.aes.erp.organogram_system.dto.APIResponse;
import com.aes.erp.organogram_system.dto.FSReturnObject;
import com.aes.erp.organogram_system.dto.OrganogramRequestDto;
import com.aes.erp.organogram_system.dto.OrganogramSystemDTO;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import com.aes.erp.organogram_system.repository.DepartmentRepository;

import com.aes.erp.organogram_system.repository.RoleNodeRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class OrganogramController {
// TODO: 18-Jul-22 move all crud calls to services

//    private final FsObjectRepository fsObjectRepository;
    private final DepartmentRepository departmentRepository;
    private final RoleNodeRepository roleNodeRepository;
    private final OrganogramHelper organogramHelper;
    private final ConsoleHelper consoleHelper;
    private final OrganogramSystemOpsDepartment organogramSystemOpsDepartment;
    private final OrganogramSystemOpsRole organogramSystemOpsRole;

    @PostMapping(value = "/{organizationId}/organogram-system")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public APIResponse organogramSystemExecuteCommand(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganogramSystemDTO organogramSystemDTO
    ) {

//        FsObject organizationObj = fsObjectRepository.findByCoreOrgDbId(organizationId);
//        if (!(organizationObj instanceof OrganizationFile organizationFile)) {
//            return new APIResponse(
//                    "Organization does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
//        }

        Optional<Department> department = departmentRepository.findById(organogramSystemDTO.getDepartmentId());
        Optional<RoleNode> roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId());

//        ConsoleCurrentState.currentDepartment = department;
//        ConsoleCurrentState.currentRoleNode = roleNode;

        // Check if currentFolder ID valid
        if (department.isEmpty()) {
            return new APIResponse(
                    "Parent does not exist",
                    false,
                    null,
                    APIResponseStatus.ERROR
            );
        }
        if (roleNode.isEmpty()) {
            return new APIResponse(
                    "Role node not found",
                    false,
                    null,
                    APIResponseStatus.ERROR
            );
        }


        Department rootDepartment = organogramSystemOpsDepartment.getRootOfDepartment(department.get());
//        if (organizationFile.getDepartment().getId().longValue() != rootDepartment.getId().longValue()) {
//            return new APIResponse(
//                    "Department does not belong to this organization",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
//        }

        FSReturnObject fsReturnObject = executeCommand(
                organogramSystemDTO.getCommand(),
                organogramSystemDTO.getArguments(),
                department.get(),
                roleNode.get()
        );

        return fsReturnObject.convertToAPIResponse();
    }

    public FSReturnObject executeCommand(String service, String arg, Department currentDepartment,
                                         RoleNode roleNode) {
        FSReturnObject fsReturnObject = new FSReturnObject();

        switch (service) {

            // ================ CONSOLE ONLY ================

            case "cd" -> {
                Department changedDepartment = organogramSystemOpsDepartment.traverse(arg, currentDepartment);
                if (changedDepartment == null) {
                    fsReturnObject = new FSReturnObject().setReturnObject(
                            false,
                            "Department is not present: " + arg,
                            null
                    );
                } else {
                    fsReturnObject = new FSReturnObject().setReturnObject(
                            true,
                            "Department changed to: " + changedDepartment.getName(),
                            changedDepartment
                    );
//                    ConsoleCurrentState.currentDepartment = changedDepartment;
//                    ConsoleCurrentState.currentRoleNode = changedDepartment.getRootRoleNode();
                }
            }
            case "x" -> fsReturnObject.setReturnObject(true, "", null);

            // ================ both console and api ================

            case "ls" -> fsReturnObject = new FSReturnObject().setReturnObject(
                    true,
                    "",""
//                    organogramHelper.printGraph(
//                            organogramSystemOpsDepartment.getRootOfDepartment(currentDepartment),
//                            currentDepartment,
//                            roleNode
//                    )
            );
            case "desc" -> fsReturnObject = new FSReturnObject().setReturnObject(
                    true,
                    "",
                    organogramSystemOpsDepartment.printCurrentDepartment(
                            currentDepartment,
                            roleNode
                    )
            );
//            case "asd" -> fsReturnObject = organogramSystemOpsDepartment.addChildDepartment(currentDepartment, arg);
//            case "arn" ->
//                    fsReturnObject = organogramSystemOpsRole.addChildNode(
//                            roleNode,
//                            arg,
//                            currentDepartment);
            case "crn" -> {
                RoleNode traversedNode = organogramSystemOpsRole.traverse(
                        arg,
                        roleNode
                );
                if (traversedNode == null) {
                    fsReturnObject = consoleHelper.getFailedReturnObject("RoleNode not found: " + arg);
                } else {
                    fsReturnObject = new FSReturnObject().setReturnObject(
                            true,
                            "RoleNode is changed to: " + traversedNode.getName(),
                            traversedNode
                    );
//                    ConsoleCurrentState.currentRoleNode = traversedNode;
                }
            }
//            case "rmsd" -> fsReturnObject = organogramSystemOpsDepartment.removeChildDepartment(currentDepartment, arg);
//            case "rmrn" -> fsReturnObject = organogramSystemOpsRole.removeChildRole(roleNode, arg);
//            case "upsd" -> fsReturnObject = organogramSystemOpsDepartment.updateDepartment(currentDepartment, arg);
//            case "uprn" -> fsReturnObject = organogramSystemOpsRole.updateRole(roleNode, arg);
            default -> fsReturnObject.setReturnObject(false, "Invalid command!", null);
        }
        return fsReturnObject;
    }

    @PostMapping(value = "/{organizationId}/organogram-system/cd")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public APIResponse organogramTraverse(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        APIResponse apiResponse = isValidOrganogram(organizationId, organogramSystemDTO);
        if (apiResponse != null) return apiResponse;

        Department currentDepartment = departmentRepository.findById(organogramSystemDTO.getDepartmentId()).orElse(null);
        Department changedDepartment = organogramSystemOpsDepartment.traverse(organogramSystemDTO.getName(), currentDepartment);

        FSReturnObject fsReturnObject;
        if (changedDepartment == null) {
            fsReturnObject = new FSReturnObject().setReturnObject(
                    false,
                    "Department is not present: " + organogramSystemDTO.getName(),
                    null
            );
        } else {
            fsReturnObject = new FSReturnObject().setReturnObject(
                    true,
                    "Department changed to: " + changedDepartment.getName(),
                    changedDepartment
            );
//            ConsoleCurrentState.currentDepartment = changedDepartment;
//            ConsoleCurrentState.currentRoleNode = changedDepartment.getRootRoleNode();
        }
        return fsReturnObject.convertToAPIResponse();
    }

    @PostMapping(value = "/organogram-system/ls")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public ResponseEntity<?> organogramPrintGraph(
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        isValidOrganogram(organogramSystemDTO);

        Department currentDepartment = departmentRepository.findById(organogramSystemDTO.getDepartmentId()).orElse(null);
        RoleNode roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId()).orElse(null);

        return new ResponseEntity<>(
                organogramSystemOpsDepartment.getRootOfDepartment(currentDepartment),
//                organogramHelper.printGraph(
//                        organogramSystemOpsDepartment.getRootOfDepartment(currentDepartment),
//                        currentDepartment,
//                        roleNode
//                ),
                HttpStatus.OK
        );
//        return new FSReturnObject().setReturnObject(true, "",""
////               organogramHelper.printGraph(organogramSystemOpsDepartment.getRootOfDepartment(currentDepartment),
////                        currentDepartment, roleNode)
//        ).convertToAPIResponse();
    }

    @PostMapping(value = "/{organizationId}/organogram-system/desc")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public APIResponse organogramPrintCurrentGraph(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        APIResponse apiResponse = isValidOrganogram(organizationId, organogramSystemDTO);
        if (apiResponse != null) return apiResponse;

        Department currentDepartment = departmentRepository.findById(organogramSystemDTO.getDepartmentId()).orElse(null);
        RoleNode roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId()).orElse(null);

        return new FSReturnObject().setReturnObject(
                true,
                "",
                organogramSystemOpsDepartment.printCurrentDepartment(currentDepartment, roleNode)
        ).convertToAPIResponse();
    }

    @PostMapping(value = "/organogram-system/asd")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_EMPLOYEE')")
    public ResponseEntity<?> organogramAddChildDepartment(
             @Valid @RequestBody OrganogramRequestDto organogramRequestDto
    ) {
//        APIResponse apiResponse = 
                isValidOrganogram(organogramRequestDto);
//        if (apiResponse != null) return apiResponse;

        Department currentDepartment = departmentRepository.findById(organogramRequestDto.getDepartmentId()).orElse(null);
//        OrganizationFile organizationFile = (OrganizationFile) fsObjectRepository.findById(organizationId).orElse(null);
        organogramSystemOpsDepartment.addChildDepartment(currentDepartment, organogramRequestDto.getName());
//        return fsReturnObject.convertToAPIResponse();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PostMapping(value = "/organogram-system/arn")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public ResponseEntity<?> organogramAddChildNode(
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
//        APIResponse apiResponse =
                isValidOrganogram(organogramSystemDTO);
//        if (apiResponse != null) return apiResponse;

        Department currentDepartment = departmentRepository.findById(organogramSystemDTO.getDepartmentId()).orElse(null);
        RoleNode roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId()).orElse(null);
//        OrganizationFile organizationFile = (OrganizationFile) fsObjectRepository.findById(organizationId).orElse(null);
         organogramSystemOpsRole.addChildNode(roleNode, organogramSystemDTO.getName(), currentDepartment);
//        return fsReturnObject.convertToAPIResponse();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping(value = "/{organizationId}/organogram-system/crn")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public APIResponse organogramTraverseNode(
            @PathVariable Long organizationId,
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        APIResponse apiResponse = isValidOrganogram(organizationId, organogramSystemDTO);
        if (apiResponse != null) return apiResponse;

        FSReturnObject fsReturnObject = new FSReturnObject();
        RoleNode roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId()).orElse(null);
        RoleNode traversedNode = organogramSystemOpsRole.traverse(
                organogramSystemDTO.getName(),
                roleNode
        );
        if (traversedNode == null) {
            fsReturnObject = consoleHelper.getFailedReturnObject("RoleNode not found: " + organogramSystemDTO.getName());
        } else {
            fsReturnObject = new FSReturnObject().setReturnObject(
                    true,
                    "RoleNode is changed to: " + traversedNode.getName(),
                    traversedNode
            );
//            ConsoleCurrentState.currentRoleNode = traversedNode;
        }
        return fsReturnObject.convertToAPIResponse();
    }

    @PostMapping(value = "/organogram-system/rmsd")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public ResponseEntity<?> organogramRemoveChildDepartment(
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO// , BindingResult result
    ) {
        isValidOrganogram(organogramSystemDTO);
//        if (apiResponse != null) return apiResponse;

        Department currentDepartment = departmentRepository.findById(organogramSystemDTO.getDepartmentId()).orElse(null);
        organogramSystemOpsDepartment.removeChildDepartment(currentDepartment, organogramSystemDTO.getName());
//        return fsReturnObject.convertToAPIResponse();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/organogram-system/rmrn")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public ResponseEntity<?> organogramRemoveChildRole(
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        isValidOrganogram(organogramSystemDTO);
//        if (apiResponse != null) return apiResponse;

        RoleNode roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId()).orElse(null);
        organogramSystemOpsRole.removeChildRole(roleNode, organogramSystemDTO.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/organogram-system/upsd")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public ResponseEntity<?> organogramUpdateDepartment(
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        isValidOrganogram(organogramSystemDTO);
//        if (apiResponse != null) return apiResponse;

        Department currentDepartment = departmentRepository.findById(organogramSystemDTO.getDepartmentId()).orElse(null);
        organogramSystemOpsDepartment.updateDepartment(currentDepartment, organogramSystemDTO.getName());
//        return fsReturnObject.convertToAPIResponse();
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/organogram-system/uprn")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN','ROLE_ENGINEER','ROLE_USER')")
    public ResponseEntity<?> organogramUpdateRole(
            @Valid @RequestBody OrganogramRequestDto organogramSystemDTO
    ) {
        isValidOrganogram(organogramSystemDTO);
//        if (apiResponse != null) return apiResponse;

        RoleNode roleNode = roleNodeRepository.findById(organogramSystemDTO.getRoleNodeId()).orElse(null);
        organogramSystemOpsRole.updateRole(roleNode, organogramSystemDTO.getName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        return fsReturnObject.convertToAPIResponse();
    }



    APIResponse isValidOrganogram(Long organizationId, OrganogramRequestDto  organogramRequestDto) {
        Department department;
        RoleNode roleNode;

        // FsObject organizationObj = fsObjectRepository.findById(organizationId).orElse(null);
//        FsObject organizationObj = fsObjectRepository.findByCoreOrgDbId(organizationId);
//        if (!(organizationObj instanceof OrganizationFile organizationFile)) {
//            return new APIResponse(
//                    "Organization does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
//        }

        department = departmentRepository.findById(organogramRequestDto.getDepartmentId()).orElse(null);
        roleNode = roleNodeRepository.findById(organogramRequestDto.getRoleNodeId()).orElse(null);

//        ConsoleCurrentState.currentDepartment = department;
//        ConsoleCurrentState.currentRoleNode = roleNode;

        // Check if currentFolder ID valid
        if (department == null) {
            throw new AesException("Parent does not exist.");
//            return new APIResponse(
//                    "Parent does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
        }
        if (organogramRequestDto.getRoleNodeId() != null && roleNode == null) {

            throw new AesException("Role does not exist.");
//            return new APIResponse(
//                    "Role does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
        }

//        if (roleNode.getParentDepartment() == null || !roleNode.getParentDepartment().getId().equals(department.getId())) {
//            throw new AesException("role node is not under given department");
//        }

        Department rootDepartment = organogramSystemOpsDepartment.getRootOfDepartment(department);
//        if (organizationFile.getDepartment().getId().longValue() != rootDepartment.getId().longValue()) {
//            return new APIResponse(
//                    "Department does not belong to this organization",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
//        }

        return null;
    }

    APIResponse isValidOrganogram(OrganogramRequestDto  organogramRequestDto) {
        Department department;
        RoleNode roleNode;

        // FsObject organizationObj = fsObjectRepository.findById(organizationId).orElse(null);
//        FsObject organizationObj = fsObjectRepository.findByCoreOrgDbId(organizationId);
//        if (!(organizationObj instanceof OrganizationFile organizationFile)) {
//            return new APIResponse(
//                    "Organization does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
//        }

        department = departmentRepository.findById(organogramRequestDto.getDepartmentId()).orElse(null);
        roleNode = roleNodeRepository.findById(organogramRequestDto.getRoleNodeId()).orElse(null);

//        ConsoleCurrentState.currentDepartment = department;
//        ConsoleCurrentState.currentRoleNode = roleNode;

        // Check if currentFolder ID valid
        if (department == null) {
            throw new AesException("Parent does not exist.");
//            return new APIResponse(
//                    "Parent does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
        }
        if (organogramRequestDto.getRoleNodeId() != null && roleNode == null) {

            throw new AesException("Role does not exist.");
//            return new APIResponse(
//                    "Role does not exist",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
        }

//        if (roleNode.getParentDepartment() == null || !roleNode.getParentDepartment().getId().equals(department.getId())) {
//            throw new AesException("role node is not under given department");
//        }

        Department rootDepartment = organogramSystemOpsDepartment.getRootOfDepartment(department);
//        if (organizationFile.getDepartment().getId().longValue() != rootDepartment.getId().longValue()) {
//            return new APIResponse(
//                    "Department does not belong to this organization",
//                    false,
//                    null,
//                    APIResponseStatus.ERROR
//            );
//        }

        return null;
    }

//    public FSReturnObject serviceInput(String commandString) {
//        FSReturnObject fsReturnObject = executeCommand(
//                consoleHelper.getService(commandString),
//                consoleHelper.getArgument(commandString),
//                ConsoleCurrentState.currentDepartment,
//                ConsoleCurrentState.currentRoleNode,
//                ConsoleCurrentState.organizationFile
//        );
//
//        if (fsReturnObject != null) {
//            System.out.println((fsReturnObject.isSuccess()
//                    ? "[SUCCESS]\n"
//                    : "[ERROR]\n") + fsReturnObject.getMessage()
//            );
//        }
//        return fsReturnObject;
//    }
}
