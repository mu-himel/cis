package com.aes.erp.authentication.dto;

import lombok.Data;

@Data
public class EmployeeInfoDto {
    private Long id;
    private String employeeId;
    private String name;
    private Long departmentId;
    private Integer level;
    private String departmentName;
    private Long designationId;
    private String designationName;
    private Long reportingManagerId;
    private String reportingManagerName;
    private Long parentDepartmentId;
}
