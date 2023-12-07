package com.aes.erp.authentication;

import java.security.Principal;

public class OrganizationPrincipal implements Principal {
    private final String orgId;

    public OrganizationPrincipal(String orgId) {
        this.orgId = orgId;
    }

    @Override
    public String getName() {
        return orgId;
    }
}
