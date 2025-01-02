package com.aes.erp.inventory.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class OrganizationCreateDto {
    private String name;
    @NotBlank(message = "code is required")
    private String code;

    private String serviceIpAddress;
    private String scmIpAddress;
    private String accIpAddress;
    private String realm;
    private String clientId;
    private String clientSecret;
    private String serviceUsername;
    private String servicePassword;

}
