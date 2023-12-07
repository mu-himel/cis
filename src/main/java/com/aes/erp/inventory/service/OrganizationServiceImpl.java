package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.OrganizationCreateDto;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.OrganizationStatus;
import com.aes.erp.inventory.repository.OrganizationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService{
    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization registerOrganization(OrganizationCreateDto dto) {
        if(dto.getName() == null || dto.getName().isEmpty()){
            throw new AesException("Organization name is required");
        }
        Optional<Organization> organizationOptional = organizationRepository.findByName(dto.getName());
        if(organizationOptional.isPresent()){
            throw new AesException("Organization is already registered");
        }
        Organization organization = new Organization();
        organization.setName(dto.getName());
        organization.setStatus(OrganizationStatus.ENABLED);
        return organizationRepository.save(organization);
    }

    @Override
    public boolean isOrganizationExistAndEnabled(Long Id) {
        Optional<Organization> organization = organizationRepository.findById(Id);
        if(organization.isPresent() && organization.get().getStatus().equals(OrganizationStatus.ENABLED))return true;
        else return false;
    }

    @Override
    public Page<?> getAllOrganization(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return organizationRepository.findAllOrganizations(pageable);
    }

    @Override
    public Organization getOrganizationById(Long orgId) {
        Optional<Organization> organization = organizationRepository.findById(orgId);
        if(!organization.isPresent()){
            throw new AesException("Organization couldn't found by given Id");
        }
        return organizationRepository.findById(orgId).get();
    }

    @Override
    public void enableOrganization(Boolean status, Long id) {
        Organization org = getOrganizationById(id);
        if(status)org.setStatus(OrganizationStatus.ENABLED);
        else org.setStatus(OrganizationStatus.DISABLED);
        organizationRepository.save(org);
    }
}
