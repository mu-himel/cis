package com.aes.erp.scm.repositories;

import com.aes.erp.scm.Entities.PriceQuotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceQuotationRepository extends JpaRepository<PriceQuotation, Long> {
}
