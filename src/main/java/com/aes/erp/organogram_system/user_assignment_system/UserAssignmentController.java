package com.aes.erp.organogram_system.user_assignment_system;

import com.aes.erp.organogram_system.dto.APIResponse;
import com.aes.erp.organogram_system.user_assignment_system.dto.UserAssignmentDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RequiredArgsConstructor
@RestController
public class UserAssignmentController {

    private final UserAssignmentOps userAssignmentService;

    @PostMapping(value = "/assign-user")
    public ResponseEntity<?> assignUser(@RequestBody UserAssignmentDTO userAssignDTO) {
        return new ResponseEntity<>(
                userAssignmentService.assignUser(userAssignDTO),
                HttpStatus.OK
        );
    }

    @PutMapping(value = "/update-user/{userAssignmentId}")
    public ResponseEntity<?> updateAssignedUser(
            @RequestBody UserAssignmentDTO userAssignmentDTO,
            @PathVariable("userAssignmentId") long userAssignmentId) {

        return new ResponseEntity<>(
                userAssignmentService.updateAssignedUser(userAssignmentId, userAssignmentDTO).convertToAPIResponse(),
                HttpStatus.OK
        );
    }

    @DeleteMapping(value = "/delete-user/{userAssignmentId}")
    public APIResponse deleteAssignedUser(@PathVariable("userAssignmentId") long userAssignmentId) {
        return userAssignmentService.deleteAssignedUser(userAssignmentId).convertToAPIResponse();
    }

    @GetMapping(value = "/read-user-hierarchy/{departmentId}")
    public ResponseEntity<?> getAssignedUserTree(@PathVariable("departmentId") long departmentId) {
        return new ResponseEntity<>(userAssignmentService.getUserAssignmentTree(departmentId),HttpStatus.OK);
    }

    @GetMapping(value = "/read-users-per-role-node/{roleNodeId}")
    public APIResponse getUsersPerRoleNode(@PathVariable("roleNodeId") long roleNodeId) {
        return userAssignmentService.getUsersPerRoleNode(roleNodeId).convertToAPIResponse();

    }

    @GetMapping("/api/v1/reporting-managers/{departmentId}/{designationId}")
    public ResponseEntity<?> getReportingManagersByDepartmentAndDesignation(
            @PathVariable("departmentId") Long departmentId,
            @PathVariable("designationId") Long designationId){
        return new ResponseEntity<>(
                userAssignmentService.getManagersByDepartmentAndRoleNode(departmentId,designationId),
                HttpStatus.OK);
    }

    @GetMapping(value = "user-assignment/get-last-indexed-user")
    public APIResponse getLastIndexedUser() {
        return userAssignmentService.getLastIndexedUser().convertToAPIResponse();
    }

    @GetMapping("/users/unassigned")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN')")
    public ResponseEntity<?> getAllUnassignedUsers() {
        return new ResponseEntity<>(
                userAssignmentService.getUnassignedUsers(),
                HttpStatus.OK);
    }
}
