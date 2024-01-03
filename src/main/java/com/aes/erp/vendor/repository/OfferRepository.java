package com.aes.erp.vendor.repository;

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
}
