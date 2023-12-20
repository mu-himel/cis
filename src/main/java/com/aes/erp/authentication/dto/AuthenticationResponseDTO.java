package com.aes.erp.authentication.dto;

import com.aes.erp.vendor.enums.VendorStatus;

import java.util.List;

/**
 * Class documentation Comments to be added
 * */
public class AuthenticationResponseDTO {
    private final String jwt;

    private final long id;

    private final List<String> roles;

//    private final List<OrganizationInfo> organizationInfos;

    private UserInfoDto userinfo;
//    private final Long vendorId;
//    private final VendorStatus vendorStatus;

//    private final List<OrganizationFileResponseDTO> organizationFileInfos;

    private final String status;

    public AuthenticationResponseDTO(String jwt, long id, List<String> roles,
//                                     List<OrganizationFileResponseDTO> organizationFileInfos,
//                                     Long vendorId, VendorStatus vendorStatus,
                                     String status) {
        this.jwt = jwt;
        this.id = id;
        this.roles = roles;
//        this.vendorId = vendorId;
//        this.vendorStatus = vendorStatus;
        this.status = status;
//        this.organizationInfos = organizationInfos;
//        this.organizationFileInfos = organizationFileInfos;
    }

//    public Long getVendorId() {
//        return vendorId;
//    }
//    public VendorStatus getVendorStatus() {
//        return vendorStatus;
//    }

    public void setUserinfo(UserInfoDto userinfo) {
        this.userinfo = userinfo;
    }


    public UserInfoDto getUserinfo() {
        return userinfo;
    }

    public String getJwt() {
        return jwt;
    }

    public long getId() {
        return id;
    }

    public List<String> getRoles() {return roles;}

//    public List<OrganizationInfo> getOrganizations() {return organizationInfos;}

//    public List<OrganizationFileResponseDTO> getOrganizationFiles(){return organizationFileInfos;}

    public String getStatus() {
        return status;
    }
}
