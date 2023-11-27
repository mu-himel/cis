package com.aes.erp.organogram_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleNodeResponseDTO {
    private long id;

    private String name;

    private long departmentId;

    private String departmentName;
}
