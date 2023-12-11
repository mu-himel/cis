package com.aes.erp.authentication;

import java.security.Principal;
import java.sql.Struct;

public class OrganizationPrincipal implements Principal {
    private final String orgId;
    private final String name;

    public OrganizationPrincipal(String orgId, String name) {
        this.orgId = orgId;
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
    public Long getOrgId(){
        return Long.valueOf(orgId);
    }
}
