package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.authentication.CustomUserDetailsService;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.authentication.dto.VendorInfoDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.network.NetworkService;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderItem;
import com.aes.erp.scm.Entities.TenderParticipator;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.dto.remote.PriceQuotationDeliveryDetailDto;
import com.aes.erp.scm.dto.remote.PriceQuotationDetailReqDto;
import com.aes.erp.scm.dto.remote.PriceQuotationReqDto;
import com.aes.erp.scm.dto.remote.PriceQuotationSummaryDto;
import com.aes.erp.scm.repositories.OfferItemRepository;
import com.aes.erp.scm.repositories.PriceQuotationRepository;
import com.aes.erp.scm.repositories.TenderParticipatorRepository;
import com.aes.erp.scm.services.TenderService;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.*;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.repository.OfferNegotiatorRepository;
import com.aes.erp.vendor.repository.OfferRepository;
import com.aes.erp.vendor.service.negotiation_history.NegotiationHistoryService;
import com.aes.erp.vendor.service.participator.NegotiatorService;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private TenderParticipatorRepository tenderParticipatorRepository;

    @Autowired
    private NetworkService networkService;

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
    private void createOfferItemsFromTenderItems(Offer offer, OfferCreateDTO createDTO){
        // List<OfferItem> savedItems = new ArrayList<>();
        offer.setAitIncluded(createDTO.getAitIncluded());
        offer.setVatIncluded(createDTO.getVatIncluded());
        offer.setDeliveryChargeAmount(createDTO.getTotalDeliveryChargeAmount());
        offer.setVatAmount(createDTO.getVatAmount());
        offer.setCreditPaymentDays(createDTO.getCreditPaymentDays());
        offer.setCreditType(createDTO.getCreditType());
        offer.setMushakIncluded(createDTO.getMushakIncluded());
        offer.setFinalOfferPrice(createDTO.getFinalOfferPrice());
        offer.setNote(createDTO.getNote());
        offer.setOfferItems(createDTO.getOfferItems().stream().map(oi->{
            OfferItem offerItem = new OfferItem();
            offerItem.setEstimatedDeliveryDays(oi.getEstimatedDeliveryDays());
            offerItem.setItemQuantity(oi.getItemQuantity());
            PriceQuotation pq = new PriceQuotation();
            pq.setPricePerUnit(oi.getPriceQuotation().getPricePerUnit());
            pq.setTotalPrice(oi.getPriceQuotation().getTotalPrice());
            offerItem.setPriceQuotation(pq);
            offerItem.setProductDescription(oi.getProductDescription());
            offerItem.setSpecification(oi.getSpecification());
            offerItem.setOffer(offer);
            return offerItem;
        }).collect(Collectors.toList()));

        offer.setWarehouses(createDTO.getWarehouses().stream().map(ow->{
            OfferDeliveryDetail odd = new OfferDeliveryDetail();
            odd.setDeliveryChargeAmount(ow.getDeliveryChargeAmount());
            odd.setDeliveryChargeMode(ow.getDeliveryChargeMode());
            odd.setWarehouseId(ow.getWarehouseId());
            odd.setItems(ow.getItems().stream().map(owi->{
                OfferItemDeliveryDetail oidd = new OfferItemDeliveryDetail();
                oidd.setItemName(owi.getItemName());
                oidd.setDeliveryOrderQTY(owi.getDeliveryOrderQTY());
                oidd.setOfferDeliveryDetail(odd);
                return oidd;
            }).collect(Collectors.toList()));
            odd.setOffer(offer);
            return odd;
        }).collect(Collectors.toList()));
        // for(OfferItem item : offer.getOfferItems()){
        //     PriceQuotation priceQuotation = item.getPriceQuotation();
        //     priceQuotation = priceQuotationRepository.save(priceQuotation);
        //     item.setPriceQuotation(priceQuotation);
        //     item.setOffer(offer);
        //     item = offerItemRepository.save(item);
        //     savedItems.add(item);
        // }
        
        // offer.getOfferItems().clear();
        // offer.setOfferItems(savedItems);
    }

    /// Initiated By Vendor
    @Transactional
    @Override
    public void createInitialOffer(ClaimResponseDto loggedInUser, OfferCreateDTO createDTO, Long tenderId) {
        Map<String,Object> vendor = loggedInUser.getUserInfoDto();
        if(vendor==null){
            throw new AesException("Logged-in User is not vendor");
        }
        Offer offer = new Offer();
        
        createOfferItemsFromTenderItems(offer, createDTO);
        offer = offerRepository.save(offer);
        Tender parentTender = tenderService.getTenderById(tenderId);

        //sent price quotation to erp project
        sentPriceQuotation(loggedInUser, parentTender, offer, OfferStage.INITIAL_OFFER);

        NegotiationHistory negotiationHistory = new NegotiationHistory();
        Tender t = new Tender(parentTender.getId());
        
        negotiationHistory.setTender(t);
        offer.setTender(t);
        negotiationHistory = negotiationHistoryService.saveHistory(negotiationHistory);

        //Set Owner Parties
        Negotiator creator = new Negotiator();
        creator.setPartyType(NegotiationPartyType.NEGOTIATION_CREATOR);
        Long vendorId = Long.parseLong(vendor.get("vendorId").toString());
        creator.setVendor(new Vendor(vendorId));
        creator = negotiatorService.saveNegotiator(creator);
        negotiationHistory.addNegotiators(creator);

        OfferNegotiator newOfferParticipatorEntry = new OfferNegotiator();
        newOfferParticipatorEntry.setNegotiator(creator);
        newOfferParticipatorEntry.setOffer(offer);
        newOfferParticipatorEntry.setPartyType(OfferPartyType.OFFER_CREATOR);
        newOfferParticipatorEntry = offerNegotiatorRepository.save(newOfferParticipatorEntry);
        offer.addParticipator(newOfferParticipatorEntry);
//
//        //Set Counter Parties
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
//

        TenderParticipator tp = new TenderParticipator();
        tp.setStatus(TenderStatus.TENDER_SENT);
        tp.setVendor(new Vendor(vendorId));
        tp.setTender(t);
        tenderParticipatorRepository.save(tp);
        
        offer.setNegotiationHistory(negotiationHistory);
        offer.setOfferStage(OfferStage.INITIAL_OFFER);
// //
// //        System.out.println(parentTender.getCode());


//         offerRepository.save(offer);
    }

    @Transactional
    private void sentPriceQuotation(ClaimResponseDto loggedInUser, Tender tender, Offer offer, OfferStage offerStage){
        PriceQuotationReqDto priceQuotationReqDto = new PriceQuotationReqDto();
        Map<String,Object> vendorInfo =  loggedInUser.getUserInfoDto();
        Long VendorId = Long.parseLong(vendorInfo.get("vendorId").toString());
        String VendorName = (String)vendorInfo.get("name");
        String vendorEmail = (String)vendorInfo.get("vendorEmail");
        String vendorPhoneNo = (String)vendorInfo.get("vendorPhoneNo");
        priceQuotationReqDto.setCode(tender.getCode());
        priceQuotationReqDto.setPaymentMethod(offer.getCreditType().name());
        priceQuotationReqDto.setVendorId(VendorId);
        priceQuotationReqDto.setVendorName(VendorName);
        priceQuotationReqDto.setVendorEmail(vendorEmail);
        priceQuotationReqDto.setVendorPhoneNo(vendorPhoneNo);

        StringBuilder  deliveryChargeType = new StringBuilder();
        priceQuotationReqDto.setDetails(offer.getOfferItems().stream().map(o->{
            PriceQuotationDetailReqDto pqdrd = new PriceQuotationDetailReqDto();
            Optional<TenderItem> tenderItemOp = tender.getTenderItems().stream().filter(ti->
                ti.getProductDescription().equals(o.getProductDescription())
            ).findFirst();

            if(tenderItemOp.isEmpty()){
                throw new AesException("Sorry! Tender Item not found");
            }
            TenderItem tenderItem = tenderItemOp.get();
            pqdrd.setEstDeliveryDays(Integer.parseInt(o.getEstimatedDeliveryDays().toString()));
            pqdrd.setDeliveryDetails(tenderItem.getDeliveryDetails().stream().map(tdd->{
                PriceQuotationDeliveryDetailDto pqdd = new PriceQuotationDeliveryDetailDto();
                pqdd.setWarehouseName(tdd.getWareHouseName());
                if(offer.getDeliveryChargeAmount()!=null){
                    deliveryChargeType.append("Excluded");
                }else{
                    deliveryChargeType.append("Included");
                }
                pqdd.setDeliveryChargeType(deliveryChargeType.toString());
                pqdd.setDeliveryChargeAmount(offer.getDeliveryChargeAmount());
                return pqdd;
            }).collect(Collectors.toList()));

            pqdrd.setRfqQty(o.getItemQuantity());
            pqdrd.setUnitPrice(o.getPriceQuotation().getPricePerUnit());
            pqdrd.setItemAttribute(o.getProductDescription());
            return pqdrd;
        }).collect(Collectors.toList()));

        if(priceQuotationReqDto!=null){
            savePriceQuotationSummary(priceQuotationReqDto,offer,deliveryChargeType.toString());
            Organization organization = tender.getTenderCreator();
            String url = organization.getServiceIpAddress().replace("/api/v1","")
                                .concat("/authenticate");
            String username = organization.getServiceUsername();
            String password = organization.getServicePassword();
            String authToken = networkService.getAuthToken(url,username,password);

            if(authToken!=null){
                StringBuilder sb = new StringBuilder("/price-quotations");
                if(offerStage.equals(OfferStage.COUNTER_OFFER) ||
                    offerStage.equals(OfferStage.FINAL_OFFER)){
                    sb.append("/receive-counter");
                }
                String priceQuotationEndpoint = organization.getServiceIpAddress().concat(sb.toString());
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(authToken);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<PriceQuotationReqDto> pqPayload = new HttpEntity<>(priceQuotationReqDto,headers);
                ResponseEntity<Void> response = networkService.post(priceQuotationEndpoint,pqPayload,Void.class);
                if(response.getStatusCode()!=HttpStatus.CREATED){
                    throw new AesException("Something wrong");
                }
            }

            
        }

    }

    @Transactional
    private void savePriceQuotationSummary(PriceQuotationReqDto pqr, Offer createDTO, String deliveryCharge){
        PriceQuotationSummaryDto pqs = new PriceQuotationSummaryDto();
        pqs.setMushak(createDTO.getMushakIncluded());
        pqs.setDeliveryCharge(deliveryCharge);
        pqs.setDeliveryChargeAmount(createDTO.getDeliveryChargeAmount());
        pqs.setCreditPaymentUnit("days");
        pqs.setCreditPaymentDuration(createDTO.getCreditPaymentDays());
        pqs.setIsAitAdded(createDTO.getAitIncluded());
        pqs.setIsVatAdded(createDTO.getVatIncluded());
        pqs.setNote(createDTO.getNote());

        
        pqs.setVatPercent(createDTO.getVatAmount().toString());
        

        pqs.setSubTotalPrice(createDTO.getFinalOfferPrice());
        pqs.setTotalPrice(createDTO.getFinalOfferPrice());
        pqr.setPriceQuotationSummary(pqs);
    }

    /// Initiated By ORG
    @Transactional
    @Override
    public void createCounterOffer(ClaimResponseDto loggedInUser, OfferCreateDTO createDTO, Long tenderId) {
        Tender parentTender = tenderService.getTenderById(tenderId);
        NegotiationHistory negotiationHistory = negotiationHistoryService.getHistoryById(createDTO.getNegotiationHistoryId());

        Offer offer = genericModelMapper.map(createDTO, Offer.class);
        offer = offerRepository.save(offer);

        createOfferItemsFromTenderItems(offer,createDTO);

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

        //Sent Counter Offer To ERP
        sentPriceQuotation(loggedInUser, parentTender, offer, OfferStage.COUNTER_OFFER);
    }

    @Override
    @Transactional
    public void receiveCounterOffer(OfferCreateDTO offerCreateDTO, Long tenderId) {
        Tender parentTender = tenderService.getTenderById(tenderId);
        NegotiationHistory negotiationHistory = negotiationHistoryService.getHistoryById(offerCreateDTO.getNegotiationHistoryId());
        Offer offer = genericModelMapper.map(offerCreateDTO, Offer.class);
        offer = offerRepository.save(offer);
        createOfferItemsFromTenderItems(offer,offerCreateDTO);

        offer.setTender(parentTender);
        offer.setNegotiationHistory(negotiationHistory);

        Negotiator negotiationCreator = null;
        Negotiator negotiationCounterPart = null;
        for(Negotiator negotiator: negotiationHistory.getNegotiators()){
            if(negotiator.getPartyType().equals(NegotiationPartyType.NEGOTIATION_COUNTER_PART)){
                negotiationCreator = negotiator;
            }
            else if(negotiator.getPartyType().equals(NegotiationPartyType.NEGOTIATION_CREATOR)){
                negotiationCounterPart = negotiator;
            }
        }

        OfferNegotiator newOfferParticipatorEntry = new OfferNegotiator();
        if(negotiationCreator != null) newOfferParticipatorEntry.setNegotiator(negotiationCreator);
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
        createOfferItemsFromTenderItems(offer, createDTO);

        Tender parentTender = tenderService.getTenderById(tenderId);
        offer.setTender(parentTender);

    }
}
