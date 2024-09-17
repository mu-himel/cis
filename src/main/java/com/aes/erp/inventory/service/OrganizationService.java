package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.OrganizationCreateDto;
import com.aes.erp.inventory.entity.Organization;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface OrganizationService {
    Organization registerOrganization(OrganizationCreateDto dto);
    boolean isOrganizationExistAndEnabled(Long Id);
    Page<?> getAllOrganization(Optional<Integer> page, Optional<Integer> size);
    Organization getOrganizationById(Long orgId);
    void enableOrganization(Boolean enable, Long id);
    List<?> getOrganizationIdbyName(Optional<String> name);

    List<Organization> getAllOrganizations();
    void sentVendorApprovedSignal();
}
