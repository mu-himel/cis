package com.aes.erp.vendor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferDeliveryDetail;

@Repository
public interface OfferDeliveryDetailRepository extends JpaRepository<OfferDeliveryDetail, Long>{
    OfferDeliveryDetail findByWarehouseIdAndOfferId(Long warehouseId, Long offerId);
}
