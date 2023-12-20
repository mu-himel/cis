package com.aes.erp.authentication.dto;

import com.aes.erp.employee.service.EmployeeService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClaimResponseDto {
    private String sub;
    private Long id;
    private Long exp;
    private Long iat;
    private List<AuthorityDto> authorities;
    private Map<String,Object> userInfoDto;
}
