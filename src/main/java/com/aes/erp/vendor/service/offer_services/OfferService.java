package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;

import java.util.List;
import java.util.Optional;

public interface OfferService {
    void createInitialOffer(ClaimResponseDto loggedInUser, OfferCreateDTO createDTO, Long tenderId);
    void createCounterOffer(ClaimResponseDto loggedInUser, OfferCreateDTO createDTO, Long tenderId);
    Optional<Offer> getById(Long id);
    List<Offer> getAllOffersByNegotiationHistoryId(Long id);
    void counterOfferByVendor(OfferCreateDTO createDTO, Long tenderId);

    Long receiveCounterOffer(OfferCreateDTO offerCreateDTO, String tenderNo);
    void lockOffer(Long id,Long vendorId);
    void lockOffer(ClaimResponseDto loggedInUser, Long id);
    void declineOffer(Long id, Long vendorId,NoteDto noteDto);
    void declineOffer(ClaimResponseDto loggedInUser, Long id,NoteDto noteDto);

    void getAwardedSignal(Long offerId, Long vendorId);
}
