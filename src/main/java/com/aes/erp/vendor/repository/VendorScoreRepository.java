package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.VendorScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorScoreRepository extends JpaRepository<VendorScore, Long> {
}
