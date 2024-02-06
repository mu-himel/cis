package com.aes.erp.inventory.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrganizationCreateDto {
    private String name;
    @NotBlank(message = "code is required")
    private String code;

    private String serviceIpAddress;
    private String serviceUsername;
    private String servicePassword;

}
