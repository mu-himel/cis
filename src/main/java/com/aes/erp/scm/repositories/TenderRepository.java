package com.aes.erp.scm.repositories;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import com.fasterxml.jackson.annotation.JsonFormat;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long>, TenderQuery {
    Page<Tender> findAll(Specification<Tender> specification, Pageable pageable);

    @Query(value = tenderProjectionQuery, countQuery = tenderProjectionCountQuery)
    Page<TenderExt> findAllTenderProjection(@Param("searchFilter") String searchFilter,
                                            @Param("subCategoryIds") List<Long> subCategoryIds,
                                            @Param("tenderType") Optional<TenderType> tenderType,
                                            @Param("startDate") Optional<Long> startDate,
                                            @Param("endDate") Optional<Long> endDate,
                                            @Param("currentDateTime") Long currentDateTime,
                                            Pageable pageable);

    interface TenderExt{
        Long getId();
        String getTenderNo();
        TenderStatus getTenderStatus();
        TenderType getTenderType();
        String getCategory();
        String getTenderCreator();
        Long getCreationDate();
        Long getTenderItemCount();

        
        Long getDeadline();
    }

    @Query(value = """
            SELECT 
                op.id as id, v.name as vendorName, o2.name as orgName, 
                op.party_type as partyType, op.organization_id as organizationId, op.vendor_id as vendorId, 
                op.participator_id  as participatorId
            FROM offer_participator op 
            LEFT JOIN offers o ON op.offer_id  = o.id
            LEFT JOIN vendor v ON op.vendor_id = v.id 
            LEFT JOIN organizations o2 ON op.organization_id = o2.id 
            WHERE o.tender_id = :tenderId
            """, nativeQuery = true)
    List<NegotiationHistoryInfo> getNegotiationHistoriesByTender(@Param("tenderId") Long id);

    /**
     * NegotiationHistoryInfo
     */ 
    public interface NegotiationHistoryInfo {
        Long getId();
        String getVendorName();
        String getOrgName();
        String getPartyType();
        Long getOrganizationId();
        Long getVendorId();
        Long getParticipatorId();
    }
}
