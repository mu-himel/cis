package com.aes.erp.authentication.dto;

import com.aes.erp.employee.service.EmployeeService;
import lombok.Data;

import java.util.List;

@Data
public class ClaimResponseDto {
    private String sub;
    private Long id;
    private Long exp;
    private Long iat;
    private List<AuthorityDto> authorities;
    private EmployeeInfoDto employee;
}
