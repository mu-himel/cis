package com.aes.erp.organogram_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrganogramSystem1DTO {

    private String arguments;
    private Long departmentId;
    private Long roleNodeId;
}
