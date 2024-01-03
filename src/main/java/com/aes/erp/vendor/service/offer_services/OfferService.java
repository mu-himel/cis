package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;

import java.util.List;
import java.util.Set;

public interface OfferService {
    void createInitialOffer(OfferCreateDTO createDTO, Long tenderId);
    void createCounterOffer(OfferCreateDTO createDTO, Long tenderId);
    Offer getById(Long id);
    List<Offer> getAllOffersByNegotiationHistoryId(Long id);
    void counterOfferByVendor(OfferCreateDTO createDTO, Long tenderId);
}
