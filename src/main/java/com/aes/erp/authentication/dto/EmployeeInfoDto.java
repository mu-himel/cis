package com.aes.erp.authentication.dto;

import com.aes.erp.user_management.service.UserRepository;
import lombok.Data;

@Data
public class EmployeeInfoDto extends UserInfoDto {
    private String employeeId;
    private Long departmentId;
    private Integer level;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private Long reportingManagerId;
    private String reportingManagerName;
    private Long parentDepartmentId;
}
