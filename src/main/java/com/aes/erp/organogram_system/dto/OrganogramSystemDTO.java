package com.aes.erp.organogram_system.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
@Data
@AllArgsConstructor
public class OrganogramSystemDTO {

    @NotBlank(message = "This is a required field.")
    private String command;
    private String arguments;

    private Long departmentId;
    private Long roleNodeId;
}
