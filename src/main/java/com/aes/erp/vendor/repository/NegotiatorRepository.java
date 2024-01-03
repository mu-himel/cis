package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.RFQ_Negotiation.Negotiator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NegotiatorRepository extends JpaRepository<Negotiator, Long> {
}
