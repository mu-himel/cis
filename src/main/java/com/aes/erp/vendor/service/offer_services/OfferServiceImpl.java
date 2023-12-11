package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.authentication.CustomUserDetailsService;
import com.aes.erp.exception.AesException;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.repositories.OfferItemRepository;
import com.aes.erp.scm.repositories.PriceQuotationRepository;
import com.aes.erp.scm.services.TenderService;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.*;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.repository.NegotiationHistoryRepository;
import com.aes.erp.vendor.repository.OfferParticipatorRepository;
import com.aes.erp.vendor.repository.OfferRepository;
import com.aes.erp.vendor.service.VendorService;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OfferServiceImpl implements OfferService{

    private final GenericModelMapper genericModelMapper;
    private final PriceQuotationRepository priceQuotationRepository;
    private final OfferItemRepository offerItemRepository;
    private final OfferRepository offerRepository;
    private final TenderService tenderService;
    private final NegotiationHistoryRepository negotiationHistoryRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final VendorService vendorService;
    private final OfferParticipatorRepository offerParticipatorRepository;

    public OfferServiceImpl(GenericModelMapper genericModelMapper, PriceQuotationRepository priceQuotationRepository,
                            OfferItemRepository offerItemRepository, OfferRepository offerRepository, TenderService tenderService,
                            NegotiationHistoryRepository negotiationHistoryRepository, CustomUserDetailsService customUserDetailsService,
                            VendorService vendorService, OfferParticipatorRepository offerParticipatorRepository) {
        this.genericModelMapper = genericModelMapper;
        this.priceQuotationRepository = priceQuotationRepository;
        this.offerItemRepository = offerItemRepository;
        this.offerRepository = offerRepository;
        this.tenderService = tenderService;
        this.negotiationHistoryRepository = negotiationHistoryRepository;
        this.customUserDetailsService = customUserDetailsService;
        this.vendorService = vendorService;
        this.offerParticipatorRepository = offerParticipatorRepository;
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
        //Set Parties
        OfferParticipator creator = new OfferParticipator();
        creator.setPartyType(PartyType.CREATOR);
        creator.setVendor(customUserDetailsService.getLoggedInVendor());
        creator = offerParticipatorRepository.save(creator);
        offer.addParticipator(creator);

        OfferParticipator counterParty = new OfferParticipator();
        counterParty.setPartyType(PartyType.COUNTER);
        counterParty.setOrganization(parentTender.getTenderCreator());
        counterParty = offerParticipatorRepository.save(counterParty);
        offer.addParticipator(counterParty);

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
        offer = offerRepository.save(offer);
        for (OfferItem item : offer.getOfferItems()){
            item.setOffer(offer);
            offerItemRepository.save(item);
        }
    }

    @Override
    public void createCounterOffer(OfferCreateDTO createDTO, Long tenderId) {
        Tender parentTender = tenderService.getTenderById(tenderId);
        Optional<NegotiationHistory> negotiationHistoryOptional;
        negotiationHistoryOptional = negotiationHistoryRepository.getByTenderId(tenderId);
        if(negotiationHistoryOptional.isEmpty()) throw new AesException("No Negotiation History found with this tender id " + tenderId );
        NegotiationHistory negotiationHistory = negotiationHistoryOptional.get();

        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        offer.setTender(parentTender);
        offer.setNegotiationHistory(negotiationHistory);

        //Set Parties
        OfferParticipator creator = new OfferParticipator();
        creator.setPartyType(PartyType.CREATOR);
        creator.setOrganization(parentTender.getTenderCreator());
        creator = offerParticipatorRepository.save(creator);
        offer.addParticipator(creator);

        OfferParticipator counterParty = new OfferParticipator();
        counterParty.setPartyType(PartyType.COUNTER);
        Vendor counterPartyVendor = vendorService.getById(createDTO.getCounterOfferVendorId());
        counterParty.setVendor(counterPartyVendor);
        counterParty = offerParticipatorRepository.save(counterParty);
        offer.addParticipator(counterParty);

        List<OfferItem> savedItems = new ArrayList<>();
        for(OfferItem item : offer.getOfferItems()){
            PriceQuotation priceQuotation = item.getPriceQuotation();
            priceQuotation = priceQuotationRepository.save(priceQuotation);
            item.setPriceQuotation(priceQuotation);
            savedItems.add(item);
        }
        offer.getOfferItems().clear();
        offer.setOfferItems(savedItems);
        offer.setOfferStage(OfferStage.COUNTER_OFFER);
        Vendor vendor = vendorService.getById(createDTO.getCounterOfferVendorId());
        offerRepository.save(offer);
        for (OfferItem item : offer.getOfferItems()){
            item.setOffer(offer);
            offerItemRepository.save(item);
        }
    }

    @Override
    public Offer getById(Long id) {
       Optional<Offer> offer = offerRepository.findById(id);
       if(offer.isEmpty()) throw new AesException("Offer couldn't be found for this id");
       return offer.get();
    }
}
