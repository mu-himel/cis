package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;

public interface OfferService {
    void createInitialOffer(OfferCreateDTO createDTO, Long tenderId);
    void createCounterOffer(OfferCreateDTO createDTO, Long tender);
    Offer getById(Long id);
}
