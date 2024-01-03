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
import com.aes.erp.vendor.repository.OfferNegotiatorRepository;
import com.aes.erp.vendor.repository.OfferRepository;
import com.aes.erp.vendor.service.negotiation_history.NegotiationHistoryService;
import com.aes.erp.vendor.service.participator.NegotiatorService;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class OfferServiceImpl implements OfferService{

    private final GenericModelMapper genericModelMapper;
    private final TenderService tenderService;
    private final CustomUserDetailsService customUserDetailsService;
    private final NegotiationHistoryService negotiationHistoryService;
    private final NegotiatorService negotiatorService;
    private final OfferNegotiatorRepository offerNegotiatorRepository;
    private final PriceQuotationRepository priceQuotationRepository;
    private final OfferItemRepository offerItemRepository;
    private final OfferRepository offerRepository;

    public OfferServiceImpl(GenericModelMapper genericModelMapper, PriceQuotationRepository priceQuotationRepository,
                            OfferItemRepository offerItemRepository, OfferRepository offerRepository, TenderService tenderService,
                            CustomUserDetailsService customUserDetailsService,
                            NegotiationHistoryService negotiationHistoryService, NegotiatorService negotiatorService,
                            OfferNegotiatorRepository offerNegotiatorRepository) {
        this.genericModelMapper = genericModelMapper;
        this.priceQuotationRepository = priceQuotationRepository;
        this.offerItemRepository = offerItemRepository;
        this.offerRepository = offerRepository;
        this.tenderService = tenderService;
        this.customUserDetailsService = customUserDetailsService;
        this.negotiationHistoryService = negotiationHistoryService;
        this.negotiatorService = negotiatorService;
        this.offerNegotiatorRepository = offerNegotiatorRepository;
    }
    public void createOfferItemsFromTenderItems(Offer offer){
        List<OfferItem> savedItems = new ArrayList<>();
        for(OfferItem item : offer.getOfferItems()){
            PriceQuotation priceQuotation = item.getPriceQuotation();
            priceQuotation = priceQuotationRepository.save(priceQuotation);
            item.setPriceQuotation(priceQuotation);
            item.setOffer(offer);
            item = offerItemRepository.save(item);
            savedItems.add(item);
        }
        offer.getOfferItems().clear();
        offer.setOfferItems(savedItems);
    }

    /// Initiated By Vendor
    @Transactional
    @Override
    public void createInitialOffer(OfferCreateDTO createDTO, Long tenderId) {
        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        offer = offerRepository.save(offer);
        createOfferItemsFromTenderItems(offer);

        Tender parentTender = tenderService.getTenderById(tenderId);
        NegotiationHistory negotiationHistory = new NegotiationHistory();
        negotiationHistory.setTender(parentTender);

        offer.setTender(parentTender);

        //Set Owner Parties
        Negotiator creator = new Negotiator();
        creator.setPartyType(NegotiationPartyType.NEGOTIATION_CREATOR);
        creator.setVendor(customUserDetailsService.getLoggedInVendor());
        creator = negotiatorService.saveNegotiator(creator);
        negotiationHistory.addNegotiators(creator);
        OfferNegotiator newOfferParticipatorEntry = new OfferNegotiator();
        newOfferParticipatorEntry.setNegotiator(creator);
        newOfferParticipatorEntry.setOffer(offer);
        newOfferParticipatorEntry.setPartyType(OfferPartyType.OFFER_CREATOR);
        newOfferParticipatorEntry = offerNegotiatorRepository.save(newOfferParticipatorEntry);
        offer.addParticipator(newOfferParticipatorEntry);

        //Set Counter Parties
        Negotiator counterParty = new Negotiator();
        counterParty.setPartyType(NegotiationPartyType.NEGOTIATION_COUNTER_PART);
        counterParty.setOrganization(parentTender.getTenderCreator());
        counterParty = negotiatorService.saveNegotiator(counterParty);
        negotiationHistory.addNegotiators(counterParty);
        newOfferParticipatorEntry = new OfferNegotiator();
        newOfferParticipatorEntry.setNegotiator(counterParty);
        newOfferParticipatorEntry.setPartyType(OfferPartyType.OFFER_COUNTER_PART);
        newOfferParticipatorEntry.setOffer(offer);
        newOfferParticipatorEntry = offerNegotiatorRepository.save(newOfferParticipatorEntry);
        offer.addParticipator(newOfferParticipatorEntry);

        negotiationHistory = negotiationHistoryService.saveHistory(negotiationHistory);
        offer.setNegotiationHistory(negotiationHistory);
        offer.setOfferStage(OfferStage.INITIAL_OFFER);
        offerRepository.save(offer);
    }



    /// Initiated By ORG
    @Transactional
    @Override
    public void createCounterOffer(OfferCreateDTO createDTO, Long tenderId) {
        Tender parentTender = tenderService.getTenderById(tenderId);
        NegotiationHistory negotiationHistory = negotiationHistoryService.getHistoryById(createDTO.getNegotiationHistoryId());

        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        offer = offerRepository.save(offer);

        createOfferItemsFromTenderItems(offer);

        offer.setTender(parentTender);
        offer.setNegotiationHistory(negotiationHistory);

        //Set Parties
        Negotiator negotiationCreator = null;
        Negotiator negotiationCounterPart = null;
        for(Negotiator negotiator: negotiationHistory.getNegotiators()){
            if(negotiator.getPartyType().equals(NegotiationPartyType.NEGOTIATION_CREATOR)){
                negotiationCreator = negotiator;
            }
            else if(negotiator.getPartyType().equals(NegotiationPartyType.NEGOTIATION_COUNTER_PART)){
                negotiationCounterPart = negotiator;
            }

        }
        OfferNegotiator newOfferParticipatorEntry = new OfferNegotiator();
        if(negotiationCreator != null)newOfferParticipatorEntry.setNegotiator(negotiationCreator);
        newOfferParticipatorEntry.setPartyType(OfferPartyType.OFFER_CREATOR);
        newOfferParticipatorEntry.setOffer(offer);
        newOfferParticipatorEntry = offerNegotiatorRepository.save(newOfferParticipatorEntry);
        offer.addParticipator(newOfferParticipatorEntry);

        newOfferParticipatorEntry = new OfferNegotiator();
        newOfferParticipatorEntry.setNegotiator(negotiationCounterPart);
        newOfferParticipatorEntry.setPartyType(OfferPartyType.OFFER_COUNTER_PART);
        newOfferParticipatorEntry.setOffer(offer);
        newOfferParticipatorEntry = offerNegotiatorRepository.save(newOfferParticipatorEntry);
        offer.addParticipator(newOfferParticipatorEntry);

        offer.setOfferStage(OfferStage.COUNTER_OFFER);
        offerRepository.save(offer);
    }
    @Override
    public Offer getById(Long id) {
       Optional<Offer> offer = offerRepository.findById(id);
       if(offer.isEmpty()) throw new AesException("Offer couldn't be found for this id");
       return offer.get();
    }

    @Override
    public List<Offer> getAllOffersByNegotiationHistoryId(Long id) {
        return offerRepository.findAllOffersByHistoryId(id);
    }

    @Override
    public void counterOfferByVendor(OfferCreateDTO createDTO, Long tenderId) {
        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        offer = offerRepository.save(offer);
        createOfferItemsFromTenderItems(offer);

        Tender parentTender = tenderService.getTenderById(tenderId);
        offer.setTender(parentTender);

    }
}
