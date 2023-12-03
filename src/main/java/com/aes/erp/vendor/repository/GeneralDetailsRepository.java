package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralDetailsRepository extends JpaRepository<GeneralDetails, Long> {
}
