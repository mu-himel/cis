package com.aes.erp.authentication.dto;

import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.user_management.service.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfoDto extends UserInfoDto {
    private String employeeId;
    private Long departmentId;
    private EmployeeType employeeType;
    private Integer level;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private Long reportingManagerId;
    private String reportingManagerName;
    private Long parentDepartmentId;
}
