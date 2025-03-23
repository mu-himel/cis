package com.aes.erp.scm.repositories;

import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
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

    @Query(value = tenderProjectionWithFilterQuery, countQuery = tenderProjectionCountQueryFilterQuery, nativeQuery = true)
    Page<TenderExtWithFilter> findAllTenderProjectionWithFilter(@Param("vendorId") Long vendorId,
                                            @Param("searchFilter") String searchFilter,
                                            @Param("subCategoryIds") List<Long> subCategoryIds,
                                            @Param("tenderType") Optional<TenderType> tenderType,
                                            @Param("organizationId") Optional<Long> organizationId,
                                            @Param("categoryId") Optional<Long> categoryId,
                                            @Param("startDate") Long startDate,
                                            @Param("endDate") Long endDate,
                                            @Param("currentDateTime") Long currentDateTime,
                                            Pageable pageable);

    @Query(value = closedTenderProjectionQuery, countQuery = tenderProjectionCountQuery, nativeQuery = true)
    Page<TenderExt> findAllClosedTenderProjection(@Param("vendorId") Long vendorId,
                                                  @Param("searchFilter") String searchFilter,
                                                  @Param("subCategoryIds") List<Long> subCategoryIds,
                                                  @Param("tenderType") Optional<TenderType> tenderType,
                                                  @Param("organizationId") Optional<Long> organizationId,
                                                  @Param("categoryId") Optional<Long> categoryId,
                                                  @Param("tenderNo") Optional<String> tenderNo,
                                                  @Param("startDate") Long startDate,
                                                  @Param("endDate") Long endDate,
                                                  @Param("currentDateTime") Long currentDateTime,
                                                  Pageable pageable);

    Optional<Tender> findByRfqNoAndTenderCreatorId(String tenderNo, Long tenderCreatorId);

    @Query(value = getLowestBidByTenderNo, nativeQuery = true)
    List<TenderLowestBid> findLowestBidByTenderNo(String tenderNo);

    interface TenderLowestBid {
        String getRfqNo();

        String getBrandName();

        String getItemAttributeName();

        String getExtendedAttributes();

        BigDecimal getTotal();
    }

    interface TenderExt {
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

    interface TenderExtWithFilter extends TenderExt{
        Long getOrganizationId();
        // Long getItemQty();
        Long getCategoryId();

    }

    @Query(value = """
        SELECT 
        o.offer_stage as offerStage , o.is_final as isFinal , 
        tp.id as id,tp.offer_id as offerId, tp.status, v.name as vendorName,
        o2.name as orgName, t.organization_id as organizationId,  tp.vendor_id as vendorId 
        FROM tender_participators tp 
        LEFT JOIN offers o ON tp.offer_id  = o.id
        LEFT JOIN vendor v ON tp.vendor_id = v.id
        LEFT JOIN tenders t ON t.id = o.tender_id
        LEFT JOIN organizations o2  on o2.id = t.organization_id
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
        String getOfferStage();
        Boolean getIsFinal();
        Long getOrganizationId();
        Long getVendorId();
        Long getOfferId();
        String getStatus();
    }

    Optional<Tender> findByRfqNo(String tenderNo);
}
