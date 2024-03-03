package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.VendorScore;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorScoreRepository extends JpaRepository<VendorScore, Long> {

    @Query(value = """
        SELECT ROUND((vs.total_score*100)/1000) as totalScore FROM vendor_score vs 
        LEFT JOIN vendors v ON v.vendor_score_id = vs.id
        WHERE v.id = :vendorId
        """,nativeQuery = true)
    Optional<Integer> findByVendorId(@Param("vendorId") Long vendorId);
}
