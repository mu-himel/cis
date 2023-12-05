package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.vendor.dto.OfferCreateDTO;

public interface OfferService {
    void createInitialOffer(OfferCreateDTO createDTO, Long tenderId);
}
