package com.aes.erp.inventory.controller;

import com.aes.erp.inventory.dto.request.OrganizationCreateDto;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.service.OrganizationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register a Organization")
    public ResponseEntity<?> registerOrganization(@RequestBody @Valid OrganizationCreateDto createDto){
        Organization createdOrganization = organizationService.registerOrganization(createDto);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("orgId", createdOrganization.getId().toString());
        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }
    @GetMapping("/all")
    @ApiOperation(value = "Get all organizations")
    public ResponseEntity<?> getAllOrganizations(@RequestParam("page") Optional<Integer> page,
                                                 @RequestParam("size") Optional<Integer> size){
        return new ResponseEntity<>(organizationService.getAllOrganization( page, size), HttpStatus.OK);
    }
}
