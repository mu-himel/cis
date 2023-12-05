package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.authentication.CustomUserDetailsService;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.repositories.OfferItemRepository;
import com.aes.erp.scm.repositories.PriceQuotationRepository;
import com.aes.erp.scm.services.TenderService;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.NegotiationHistory;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferStage;
import com.aes.erp.vendor.repository.NegotiationHistoryRepository;
import com.aes.erp.vendor.repository.OfferRepository;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OfferServiceImpl implements OfferService{

    private final GenericModelMapper genericModelMapper;
    private final PriceQuotationRepository priceQuotationRepository;
    private final OfferItemRepository offerItemRepository;
    private final OfferRepository offerRepository;
    private final TenderService tenderService;
    private final NegotiationHistoryRepository negotiationHistoryRepository;
    private final CustomUserDetailsService customUserDetailsService;

    public OfferServiceImpl(GenericModelMapper genericModelMapper, PriceQuotationRepository priceQuotationRepository, OfferItemRepository offerItemRepository, OfferRepository offerRepository, TenderService tenderService, NegotiationHistoryRepository negotiationHistoryRepository, CustomUserDetailsService customUserDetailsService) {
        this.genericModelMapper = genericModelMapper;
        this.priceQuotationRepository = priceQuotationRepository;
        this.offerItemRepository = offerItemRepository;
        this.offerRepository = offerRepository;
        this.tenderService = tenderService;
        this.negotiationHistoryRepository = negotiationHistoryRepository;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public void createInitialOffer(OfferCreateDTO createDTO, Long tenderId) {
        Tender parentTender = tenderService.getTenderById(tenderId);
        NegotiationHistory negotiationHistory = new NegotiationHistory();
        negotiationHistory.setTender(parentTender);
        negotiationHistory = negotiationHistoryRepository.save(negotiationHistory);

        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        offer.setTender(parentTender);
        offer.setNegotiationHistory(negotiationHistory);
        offer.setCounterParty(parentTender.getTenderCreator());
        List<OfferItem> savedItems = new ArrayList<>();
        for(OfferItem item : offer.getOfferItems()){
            PriceQuotation priceQuotation = item.getPriceQuotation();
            priceQuotation = priceQuotationRepository.save(priceQuotation);
            item.setPriceQuotation(priceQuotation);
            savedItems.add(item);
        }
        offer.getOfferItems().clear();
        offer.setOfferItems(savedItems);
        offer.setOfferStage(OfferStage.INITIAL_OFFER);
        offer.setOwnerParty(customUserDetailsService.getLoggedInVendor());
        offerRepository.save(offer);
        for (OfferItem item : offer.getOfferItems()){
            item.setOffer(offer);
            offerItemRepository.save(item);
        }
    }
}
