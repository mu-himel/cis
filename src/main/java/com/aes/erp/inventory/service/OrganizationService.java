package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.OrganizationCreateDto;
import com.aes.erp.inventory.entity.Organization;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface OrganizationService {
    Organization registerOrganization(OrganizationCreateDto dto);
    boolean isOrganizationExist(Long Id);
    Page<?> getAllOrganization(Optional<Integer> page, Optional<Integer> size);
}
