package com.aes.erp.vendor.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferTermsAndCondition;

@Repository
public interface OfferTermsAndConditionRepository extends JpaRepository<OfferTermsAndCondition,Long>{

    List<OfferTermsAndCondition> findAllByTenderIdAndVendorId(Long id, Long vendorId);
    
}
