package com.aes.erp.scm.repositories;

import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import com.aes.erp.vendor.entity.RFQ_Negotiation.NegotiationPartyType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long>, TenderQuery {
    Page<Tender> findAll(Specification<Tender> specification, Pageable pageable);

    @Query(value = tenderProjectionQuery, countQuery = tenderProjectionCountQuery, nativeQuery = true)
    Page<TenderExt> findAllTenderProjection(@Param("vendorId") Long vendorId,
                                            @Param("searchFilter") String searchFilter,
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
        String getItemCategory();
        String getTenderCreator();
        Long getCreationDate();
        Long getTenderItemCount();

        
        Long getDeadline();
    }

    @Query(value = """
            SELECT 
                tp.id as id,tp.offer_id as offerId, tp.status, v.name as vendorName, org.name as orgName, 
                ofn.party_type as partyType, n.organization_id as organizationId, tp.vendor_id as vendorId 
                
            FROM tender_participators tp 
            LEFT JOIN offers o ON tp.offer_id  = o.id
            LEFT JOIN vendor v ON tp.vendor_id = v.id
            LEFT JOIN offer_negotiator ofn ON ofn.offer_id = o.id 
            LEFT JOIN negotiator n ON ofn.negotiator_id = n.id 
            LEFT JOIN organizations org ON org.id = n.organization_id
            WHERE o.tender_id = :tenderId
            AND tp.vendor_id = :vendorId
            GROUP BY tp.id
            """, nativeQuery = true)
    List<NegotiationHistoryInfo> getNegotiationHistoriesByTender(
        @Param("tenderId") Long id,
        @Param("vendorId") Long vendorId
    );

    /**
     * NegotiationHistoryInfo
     */ 
    public interface NegotiationHistoryInfo {
        Long getId();
        String getVendorName();
        String getOrgName();
        NegotiationPartyType getPartyType();
        Long getOrganizationId();
        Long getVendorId();
        Long getOfferId();
        Long getParticipatorId();
        String getStatus();
    }
}
