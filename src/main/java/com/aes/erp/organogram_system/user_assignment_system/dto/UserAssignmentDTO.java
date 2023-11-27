package com.aes.erp.organogram_system.user_assignment_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAssignmentDTO {
//    private String command;
//    private String arguments;
    private Long userId;
    private Long departmentId;
    private Long roleId;
    private Long parentUserId;
}
