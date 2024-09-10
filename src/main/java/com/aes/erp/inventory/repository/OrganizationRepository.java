package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.OrganizationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {


    @Query(value = "Select * from organizations", nativeQuery = true)
    Page<OrganizationExt> findAllOrganizations(Pageable pageable);
    @Query(value = "Select * from organizations o WHERE o.name=:name", nativeQuery = true)
    Optional<Organization> findByName(@Param("name") String name);
    // @Query(value = "Select * from organizations o WHERE :name IS NULL OR LOWER(o.name) LIKE LOWER(:name)||'%'", nativeQuery = true)
    @Query(value = "Select * from organizations o WHERE LOWER(o.name) LIKE LOWER(CONCAT('%', :name, '%'))", nativeQuery = true)
    List<OrganizationExt> findByName1(@Param("name") String name);

    interface OrganizationExt{
        String getName();
        String getId();
        OrganizationStatus getStatus();
    }
}
