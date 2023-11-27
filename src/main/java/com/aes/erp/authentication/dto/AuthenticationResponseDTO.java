package com.aes.erp.authentication.dto;

import java.util.List;

/**
 * Class documentation Comments to be added
 * */
public class AuthenticationResponseDTO {
    private final String jwt;

    private final long id;

    private final List<String> roles;

    private final List<OrganizationInfo> organizationInfos;

    private EmployeeInfoDto employee;

//    private final List<OrganizationFileResponseDTO> organizationFileInfos;

    private final String status;

    public AuthenticationResponseDTO(String jwt, long id, List<String> roles, List<OrganizationInfo> organizationInfos,
//                                     List<OrganizationFileResponseDTO> organizationFileInfos,
                                     String status) {
        this.jwt = jwt;
        this.id = id;
        this.roles = roles;
        this.status = status;
        this.organizationInfos = organizationInfos;
//        this.organizationFileInfos = organizationFileInfos;
    }

    public void setEmployee(EmployeeInfoDto employee) {
        this.employee = employee;
    }

    public EmployeeInfoDto getEmployee() {
        return employee;
    }

    public String getJwt() {
        return jwt;
    }

    public long getId() {
        return id;
    }

    public List<String> getRoles() {return roles;}

    public List<OrganizationInfo> getOrganizations() {return organizationInfos;}

//    public List<OrganizationFileResponseDTO> getOrganizationFiles(){return organizationFileInfos;}

    public String getStatus() {
        return status;
    }
}
