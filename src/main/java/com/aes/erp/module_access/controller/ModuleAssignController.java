package com.aes.erp.module_access.controller;

import com.aes.erp.authentication.JwtUtil;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.module_access.dto.request.DeletePermissionRequest;
import com.aes.erp.module_access.dto.request.ModuleAccessPermissionRequest;
import com.aes.erp.module_access.dto.request.UpdateModulePermissionRequest;
import com.aes.erp.module_access.enums.ModuleAssignType;
import com.aes.erp.module_access.service.ModuleAccessPermissionService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/modules/permission")
public class ModuleAssignController {

    @Autowired
    private ModuleAccessPermissionService moduleAccessPermissionService;



    @PostMapping("/assign")
    @ApiOperation(value = "Assign Module")
    public ResponseEntity<?> assignModulePermission(
            @RequestBody ModuleAccessPermissionRequest moduleAccessPermissionRequest){

        moduleAccessPermissionService.setModuleAssignType(moduleAccessPermissionRequest.getModuleAssignType());
        moduleAccessPermissionService.addModuleAccessPermission(moduleAccessPermissionRequest);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/menus")
    public ResponseEntity<?> getPermissionWiseModules(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    ){
        return  new ResponseEntity<>(
                moduleAccessPermissionService.getPermissionWiseModules(token),
                HttpStatus.OK);
    }

    @GetMapping("/by-department/{id}")
    public ResponseEntity<?> getPermissionWiseModulesByDepartment(
            @PathVariable("id") Long id
    ){
        return  new ResponseEntity<>(
                moduleAccessPermissionService.getPermissionWiseModulesByDepartment(id),
                HttpStatus.OK);
    }

    @GetMapping("/by-designation/{departmentId}/{id}")
    public ResponseEntity<?> getPermissionWiseModulesByDesignation(
            @PathVariable("departmentId") Long departmentId,
            @PathVariable("id") Long id
    ){
        return  new ResponseEntity<>(
                moduleAccessPermissionService.getPermissionWiseModulesByDesignation(departmentId,id),
                HttpStatus.OK);
    }

    @GetMapping("/by-user/{departmentId}/{designationId}/{id}")
    public ResponseEntity<?> getPermissionWiseModulesByUser(
            @PathVariable("departmentId") Long departmentId,
            @PathVariable("designationId") Long designationId,
            @PathVariable("id") Long id
    ){
        return  new ResponseEntity<>(
                moduleAccessPermissionService
                        .getPermissionWiseModulesByUser(departmentId,designationId,id),
                HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> changePermission(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateModulePermissionRequest updateModulePermissionRequest
    ){
        moduleAccessPermissionService.updateCrudPermission(id,updateModulePermissionRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePermission(@PathVariable("id") Long id,
                                              @RequestBody DeletePermissionRequest deletePermissionRequest
                                              ){
        moduleAccessPermissionService.deletePermissionById(id,deletePermissionRequest);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



}
