package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.OrganizationCreateDto;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.OrganizationStatus;
import com.aes.erp.inventory.repository.OrganizationRepository;
import com.aes.erp.network.NetworkService;
import com.aes.erp.user_management.entity.Role;
import com.aes.erp.user_management.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService{
    private final OrganizationRepository organizationRepository;
    private final RoleService roleService;
    private final NetworkService networkService;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository, RoleService roleService,
                                   NetworkService networkService) {
        this.organizationRepository = organizationRepository;
        this.roleService = roleService;
        this.networkService = networkService;
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
        organization.setServiceIpAddress(dto.getServiceIpAddress());
        organization.setServiceUsername(dto.getServiceUsername());
        organization.setServicePassword(dto.getServicePassword());
        Role role = roleService.read("ORGANIZATION");
        organization.setRole(role);
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
        Sort sort = Sort.by(Sort.Direction.ASC,"id");
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

    @Override
    public List<?> getOrganizationIdbyName(Optional<String> name) {
        return organizationRepository.findByOrganizationName(name.orElse(null));
    }

    @Override
    public List<Organization> getAllOrganizations() {
        return organizationRepository.findAll();
    }

    @Override
    @Async
    public void sentVendorApprovedSignal() {
        getAllOrganizations().stream().forEach(org->{
            String token = networkService.getKeycloakAccessToken(org);
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            HttpEntity<Void> payload = new HttpEntity<>(headers);
            networkService.post(org.getServiceIpAddress()+"/integrations/ping",payload,Void.class);
        });
    }
}
