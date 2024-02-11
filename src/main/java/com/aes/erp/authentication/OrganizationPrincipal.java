package com.aes.erp.authentication;

import java.security.Principal;
import java.sql.Struct;

public class OrganizationPrincipal implements Principal {
    private final Long orgId;
    private final String name;

    public OrganizationPrincipal(Long orgId, String name) {
        this.orgId = orgId;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    public Long getOrgId(){
        return orgId;
    }
}
