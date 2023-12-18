package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.RFQ_Negotiation.NegotiationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NegotiationHistoryRepository extends JpaRepository<NegotiationHistory, Long> {
    @Query(value = "Select * from negotiation_history " +
            "WHERE negotiation_history.tender_id=:tenderId", nativeQuery = true)
    Optional<NegotiationHistory> getByTenderId(@Param("tenderId") Long tenderId);
}
