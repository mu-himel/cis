package com.aes.erp.vendor.repository;

import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderParticipator;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
    @Query(value = "SELECT * from offers o WHERE o.negotiation_history_id = :historyId", nativeQuery = true)
    List<Offer> findAllOffersByHistoryId(@Param("historyId") Long historyId);

    @Query(value = "SELECT tp FROM TenderParticipator tp WHERE (tp.status = :status AND tp.offer = :offer AND tp.vendor = :vendor AND tp.tender = :tender)")
    // @Query(value = "SELECT * FROM tender_participators tp WHERE tp.status = :status AND tp.offer_id = :offer AND tp.vendor_id = :vendor AND tp.tender_id = :tender", nativeQuery = true)
    TenderParticipator hasOffer(@Param("status") TenderStatus status, @Param("offer") Offer offer, @Param("vendor") Vendor vendor, @Param("tender") Tender tender);
}
