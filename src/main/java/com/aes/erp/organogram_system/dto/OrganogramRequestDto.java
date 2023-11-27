package com.aes.erp.organogram_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganogramRequestDto {

    private String name;
    private Long departmentId;
    private Long roleNodeId;
}
