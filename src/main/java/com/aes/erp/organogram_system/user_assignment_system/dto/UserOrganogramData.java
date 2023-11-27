package com.aes.erp.organogram_system.user_assignment_system.dto;

import com.aes.erp.organogram_system.user_assignment_system.entity.UserAssignment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserOrganogramData {
    private Object user;
    private Object parentUser;
    private List<UserAssignment> childs;
}
