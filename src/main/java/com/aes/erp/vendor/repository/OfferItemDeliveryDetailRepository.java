package com.aes.erp.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItemDeliveryDetail;

@Repository
public interface OfferItemDeliveryDetailRepository extends JpaRepository<OfferItemDeliveryDetail,Long>{
    
}
