package com.aes.erp.scm.repositories;

import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferItemRepository extends JpaRepository<OfferItem, Long> {
    List<OfferItem> findByOfferId(Long offerId);

}
