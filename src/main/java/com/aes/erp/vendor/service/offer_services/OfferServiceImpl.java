package com.aes.erp.vendor.service.offer_services;

import com.aes.erp.authentication.CustomUserDetailsService;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.network.NetworkService;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderItem;
import com.aes.erp.scm.Entities.TenderParticipator;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.dto.NoteDto;
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
import com.aes.erp.vendor.repository.VendorScoreRepository;
import com.aes.erp.vendor.service.negotiation_history.NegotiationHistoryService;
import com.aes.erp.vendor.service.participator.NegotiatorService;
import com.aes.erp.vendor.utils.GenericModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
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
    private VendorScoreRepository vendorScoreRepository;

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
        offer.setVatPercent(createDTO.getVatPercent());
        offer.setCreditPaymentDays(createDTO.getCreditPaymentDays());
        offer.setCreditType(createDTO.getCreditType());
        offer.setMushakIncluded(createDTO.getMushakIncluded());
        offer.setFinalOfferPrice(createDTO.getFinalOfferPrice());
        offer.setNote(createDTO.getNote());
        offer.setIsFinal(createDTO.getIsFinal());
        offer.setOfferItems(createDTO.getOfferItems().stream().map(oi->{
            OfferItem offerItem = new OfferItem();
            offerItem.setWarrantyDuration(oi.getWarrantyDuration());
            offerItem.setWarrantyUnit(oi.getWarrantyUnit());
            offerItem.setEstimatedDeliveryDays(oi.getEstimatedDeliveryDays());
            offerItem.setItemQuantity(oi.getItemQuantity());
            PriceQuotation pq = new PriceQuotation();
            pq.setPricePerUnit(oi.getPriceQuotation().getPricePerUnit());
            pq.setTotalPrice(oi.getPriceQuotation().getTotalPrice());
            offerItem.setPriceQuotation(pq);
            offerItem.setBrandName(oi.getBrandName());
            offerItem.setExtendedAttributes(oi.getExtendedAttributes());
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
        Long vendorId = Long.parseLong(vendor.get("vendorId").toString());
        if(vendor==null){
            throw new AesException("Logged-in User is not vendor");
        }
        Offer offer = new Offer();
        
        createOfferItemsFromTenderItems(offer, createDTO);
        offerRepository.save(offer);
        Tender parentTender = tenderService.getTenderById(tenderId);

        


        NegotiationHistory negotiationHistory = new NegotiationHistory();
        Tender t = new Tender(parentTender.getId());

        if(createDTO.getTermsAndConditions().size()>0){
            offer.setTermsAndConditions(createDTO.getTermsAndConditions().stream().map(termCondition->{
                OfferTermsAndCondition offerTermsAndCondition = new OfferTermsAndCondition();
                offerTermsAndCondition.setOffer(offer);
                offerTermsAndCondition.setVendor(new Vendor(vendorId));
                offerTermsAndCondition.setTender(t);
                offerTermsAndCondition.setTermsAndCondition(termCondition.getTermsAndCondition());
                return offerTermsAndCondition;
            }).collect(Collectors.toList()));
        }
        
        negotiationHistory.setTender(t);
        offer.setTender(t);
        negotiationHistory = negotiationHistoryService.saveHistory(negotiationHistory);

        //Set Owner Parties
        Negotiator creator = new Negotiator();
        creator.setPartyType(NegotiationPartyType.NEGOTIATION_CREATOR);
        
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

        offerRepository.save(offer);
//

        TenderParticipator tp = new TenderParticipator();
        tp.setStatus(TenderStatus.TENDER_SENT);
        tp.setVendor(new Vendor(vendorId));
        tp.setOffer(offer);
        tp.setTender(t);
        tenderParticipatorRepository.save(tp);
        
        offer.setNegotiationHistory(negotiationHistory);
        offer.setOfferStage(OfferStage.INITIAL_OFFER);

        //sent price quotation to erp project
        sentPriceQuotation(loggedInUser, parentTender, offer, OfferStage.INITIAL_OFFER);
// //
// //        System.out.println(parentTender.getCode());


//         offerRepository.save(offer);
    }

    @Transactional
    private void sentPriceQuotation(ClaimResponseDto loggedInUser, Tender tender, Offer offer, OfferStage offerStage){
        PriceQuotationReqDto priceQuotationReqDto = new PriceQuotationReqDto();
        Map<String,Object> vendorInfo =  loggedInUser.getUserInfoDto();
        
        Long VendorId = Long.parseLong(vendorInfo.get("vendorId").toString());
        Optional<Integer> scoreOp = vendorScoreRepository.findByVendorId(VendorId);
        Integer score=0;
        if(scoreOp.isPresent()){
            score = scoreOp.get();
        }
        
        String VendorName = (String)vendorInfo.get("name");
        String vendorEmail = (String)vendorInfo.get("vendorEmail");
        String vendorPhoneNo = (String)vendorInfo.get("vendorPhoneNo");
        String vendorType = (String) vendorInfo.get("vendorType");
        priceQuotationReqDto.setRemoteOfferId(offer.getId());
        priceQuotationReqDto.setCode(tender.getCode());
        priceQuotationReqDto.setPaymentMethod(offer.getCreditType().name());
        priceQuotationReqDto.setVendorId(VendorId);
        priceQuotationReqDto.setVendorName(VendorName);
        priceQuotationReqDto.setVendorEmail(vendorEmail);
        priceQuotationReqDto.setVendorPhoneNo(vendorPhoneNo);
        priceQuotationReqDto.setVendorType(vendorType);
        priceQuotationReqDto.setScore(score);
        priceQuotationReqDto.setNegotiationHistoryId(offer.getNegotiationHistory().getId());
        priceQuotationReqDto.setIsFinal(offer.getIsFinal());
        if(offer.getTermsAndConditions().size()>0){
            priceQuotationReqDto.setTermsAndConditions(offer.getTermsAndConditions().stream().map(otc->{
                return otc.getTermsAndCondition();
            }).collect(Collectors.toList()));
        }

        Map<String,Object>  deliveryChargeType = new HashMap<>();
        priceQuotationReqDto.setDetails(offer.getOfferItems().stream().map(o->{
           
            PriceQuotationDetailReqDto pqdrd = new PriceQuotationDetailReqDto();
            Optional<TenderItem> tenderItemOp = tender.getTenderItems().stream().filter(ti->
                ti.getProductDescription().equals(o.getProductDescription())
            ).findFirst();

            if(tenderItemOp.isEmpty()){
                throw new AesException("Sorry! Tender Item not found");
            }
            TenderItem tenderItem = tenderItemOp.get();
            pqdrd.setWarrantyDuration(o.getWarrantyDuration());
            pqdrd.setWarrantyUnit(o.getWarrantyUnit());
            pqdrd.setEstDeliveryDays(Integer.parseInt(o.getEstimatedDeliveryDays().toString()));
            pqdrd.setDeliveryDetails(tenderItem.getDeliveryDetails().stream().map(tdd->{
                
                PriceQuotationDeliveryDetailDto pqdd = new PriceQuotationDeliveryDetailDto();
                pqdd.setWarehouseName(tdd.getWareHouseName());
                if(offer.getDeliveryChargeAmount().equals(BigDecimal.valueOf(0))){
                    
                    deliveryChargeType.put("deliveryCharge","Included");
                }else{
                    deliveryChargeType.put("deliveryCharge","Excluded");
                }
              
                pqdd.setDeliveryOrderQty(tdd.getDeliveryOrderQTY());
                pqdd.setDeliveryChargeType(String.valueOf(deliveryChargeType.get("deliveryCharge")));
                pqdd.setDeliveryChargeAmount(offer.getDeliveryChargeAmount());
                return pqdd;
            }).collect(Collectors.toList()));

            pqdrd.setRfqQty(o.getItemQuantity());
            pqdrd.setUnitPrice(o.getPriceQuotation().getPricePerUnit());
            pqdrd.setBrandName(o.getBrandName());
            pqdrd.setExtendedAttributes(o.getExtendedAttributes());
            pqdrd.setItemAttribute(o.getProductDescription());
            return pqdrd;
        }).collect(Collectors.toList()));

        if(priceQuotationReqDto!=null){
            savePriceQuotationSummary(priceQuotationReqDto,offer,String.valueOf(deliveryChargeType.get("deliveryCharge")));
            Organization organization = tender.getTenderCreator();
            // String url = organization.getServiceIpAddress().replace("/api/v1","")
            //                     .concat("/authenticate");
            // String username = organization.getServiceUsername();
            // String password = organization.getServicePassword();
            // String authToken = networkService.getAuthToken(url,username,password);
            String authToken = login(organization);    
            if(authToken!=null){
                StringBuilder sb = new StringBuilder("/price-quotations");
                if(offerStage.equals(OfferStage.COUNTER_TO_COMPANY) ||
                    offerStage.equals(OfferStage.FINAL_OFFER_TO_COMPANY)){
                    sb.append("/receive-counter");
                }
                String priceQuotationEndpoint = organization.getServiceIpAddress().concat(sb.toString());
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(authToken);
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<PriceQuotationReqDto> pqPayload = new HttpEntity<>(priceQuotationReqDto,headers);
                ResponseEntity<Void> response = networkService.post(priceQuotationEndpoint,pqPayload,Void.class);
                if(!response.getStatusCode().equals(HttpStatus.NO_CONTENT) && 
                    !response.getStatusCode().equals(HttpStatus.CREATED)){
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

        pqs.setVatPercent(createDTO.getVatPercent().toString());
        pqs.setVatAmount(createDTO.getVatAmount().toString());
        

        pqs.setSubTotalPrice(createDTO.getFinalOfferPrice());
        pqs.setTotalPrice(createDTO.getFinalOfferPrice());
        pqr.setPriceQuotationSummary(pqs);
    }

    /// Initiated By ORG
    @Transactional
    @Override
    public void createCounterOffer(ClaimResponseDto loggedInUser, OfferCreateDTO createDTO, Long tenderId) {
        Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        Tender parentTender = tenderService.getTenderById(tenderId);
        NegotiationHistory negotiationHistory = negotiationHistoryService.getHistoryById(createDTO.getNegotiationHistoryId());

        Offer offer = new Offer();
        createOfferItemsFromTenderItems(offer,createDTO);
        offerRepository.save(offer);

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

        offer.setOfferStage(OfferStage.COUNTER_TO_COMPANY);
        offerRepository.save(offer);

        

        if(createDTO.getTermsAndConditions().size()>0){
            offer.setTermsAndConditions(createDTO.getTermsAndConditions().stream().map(termCondition->{
                OfferTermsAndCondition offerTermsAndCondition = new OfferTermsAndCondition();
                offerTermsAndCondition.setOffer(offer);
                offerTermsAndCondition.setVendor(new Vendor(vendorId));
                offerTermsAndCondition.setTender(parentTender);
                offerTermsAndCondition.setTermsAndCondition(termCondition.getTermsAndCondition());
                return offerTermsAndCondition;
            }).collect(Collectors.toList()));
        }

        TenderParticipator tp = new TenderParticipator();
        tp.setStatus(TenderStatus.COUNTERED);
        
        tp.setVendor(new Vendor(vendorId));
        tp.setOffer(offer);
        tp.setTender(parentTender);
        tenderParticipatorRepository.save(tp);
        //Sent Counter Offer To ERP
        sentPriceQuotation(loggedInUser, parentTender, offer, OfferStage.COUNTER_TO_COMPANY);
    }

    @Override
    @Transactional
    public Long receiveCounterOffer(OfferCreateDTO offerCreateDTO, String tenderNo) {
        Tender parentTender = tenderService.getTenderByRfqNo(tenderNo);
        NegotiationHistory negotiationHistory = negotiationHistoryService.getHistoryById(offerCreateDTO.getNegotiationHistoryId());
        Offer offer = new Offer();
        createOfferItemsFromTenderItems(offer,offerCreateDTO);
        offerRepository.save(offer);
        
        

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

        Vendor vendor = negotiationCounterPart.getVendor();

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

        if(offerCreateDTO.getTermsAndConditions().size()>0){
            offer.setTermsAndConditions(offerCreateDTO.getTermsAndConditions().stream().map(tnc->{
                OfferTermsAndCondition offerTermsAndCondition = new OfferTermsAndCondition();
                offerTermsAndCondition.setOffer(offer);
                offerTermsAndCondition.setTender(parentTender);
                offerTermsAndCondition.setTermsAndCondition(tnc.getTermsAndCondition());
                offerTermsAndCondition.setVendor(vendor);
                return offerTermsAndCondition;
            }).collect(Collectors.toList()));
        }
        

        offer.setOfferStage(OfferStage.COUNTER_TO_VENDOR);
        offerRepository.save(offer);

        TenderParticipator tp = new TenderParticipator();
        tp.setStatus(TenderStatus.COUNTERED);
        tp.setVendor(negotiationCounterPart.getVendor());
        tp.setOffer(offer);
        tp.setTender(parentTender);
        tenderParticipatorRepository.save(tp);

        return offer.getId();
    }

    @Override
    public Optional<Offer> getById(Long id) {
       Optional<Offer> offerOp = offerRepository.findById(id);
       if(offerOp.isEmpty()) throw new AesException("Offer couldn't be found for this id");
       Offer offer = offerOp.get();
       return Optional.ofNullable(offer);
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

    @Override
    @Transactional
    public void lockOffer(Long id, Long vendorId) {
        Optional<Offer> offerOp = offerRepository.findById(id);
        if(offerOp.isEmpty()){
            throw new AesException("Sorry! Offer not found");
        }
        Offer offer = offerOp.get();
        // Tender tender = offer.getTender();
        
        Optional<TenderParticipator> tpOp = tenderParticipatorRepository.findByOfferId(offer.getId());
        if(tpOp.isPresent()){
            TenderParticipator tp = tpOp.get();
            tp.setStatus(TenderStatus.LOCKED);
        }
        // TenderParticipator tp = new TenderParticipator();
        // tp.setStatus(TenderStatus.AWARDED);
        // tp.setVendor(new Vendor(vendorId));
        // tp.setOffer(offer);
        // tp.setTender(tender);
        // tenderParticipatorRepository.save(tp);
    }

    private String login(Organization organization){
       
            String url = organization.getServiceIpAddress().replace("/api/v1","")
                                .concat("/authenticate");
            String username = organization.getServiceUsername();
            String password = organization.getServicePassword();
        return networkService.getAuthToken(url,username,password);
    }

    @Override
    @Transactional
    public void declineOffer(Long id, Long vendorId,NoteDto noteDto) {
        Optional<Offer> offerOp = offerRepository.findById(id);
        if(offerOp.isEmpty()){
            throw new AesException("Sorry! Offer not found");
        }
        Offer offer = offerOp.get();
        // Tender tender = offer.getTender();
        Optional<TenderParticipator> tpOp = tenderParticipatorRepository.findByOfferId(offer.getId());
        if(tpOp.isPresent()){
            TenderParticipator tp = tpOp.get();
            tp.setStatus(TenderStatus.REJECTED);
            offer.setDeclineMessage(noteDto.getNote());
        }
        // TenderParticipator tp = new TenderParticipator();
        // tp.setStatus(TenderStatus.REJECTED);
        // tp.setVendor(new Vendor(vendorId));
        // tp.setOffer(offer);
        // tp.setTender(tender);
        // tenderParticipatorRepository.save(tp);
    }

    


    @Override
    @Transactional
    public void declineOffer(ClaimResponseDto loggedInUser, Long id, NoteDto noteDto) {
        Optional<Offer> offerOp = offerRepository.findById(id);
        if(offerOp.isEmpty()){
            throw new AesException("Sorry! Offer not found");
        }
        Offer offer = offerOp.get();
        Tender tender = offer.getTender();
        Optional<TenderParticipator> tpOp = tenderParticipatorRepository.findByOfferId(offer.getId());
        if(tpOp.isPresent()){
            offer.setDeclineMessage(noteDto.getNote());

            TenderParticipator tp = tpOp.get();
            tp.setStatus(TenderStatus.REJECTED);

            Organization organization = tender.getTenderCreator();
            String authToken = login(organization);
        
            if(authToken!=null){
                sentOfferDeclineRequest(authToken, organization, offer, noteDto);
            }
        }
    }

    @Override
    @Transactional
    public void lockOffer(ClaimResponseDto loggedInUser, Long id) {
        Optional<Offer> offerOp = offerRepository.findById(id);
        if(offerOp.isEmpty()){
            throw new AesException("Sorry! Offer not found");
        }
        Offer offer = offerOp.get();
        Tender tender = offer.getTender();
        // Long vendorId = Long.parseLong(loggedInUser.getUserInfoDto().get("vendorId").toString());
        
        Optional<TenderParticipator> tpOp = tenderParticipatorRepository.findByOfferId(offer.getId());
        if(tpOp.isPresent()){
            TenderParticipator tp = tpOp.get();
            tp.setStatus(TenderStatus.LOCKED);

            Organization organization = tender.getTenderCreator();
            String authToken = login(organization);
        
            if(authToken!=null){
                sentOfferLockRequest(authToken, organization, offer);
            }
        }
        
        
    }

    private void sentOfferLockRequest(String authToken, Organization organization,Offer offer){
        StringBuilder sb = new StringBuilder("/price-quotations");
                
            sb.append("/").append(offer.getId()).append("/lock/receive");
        
        String priceQuotationEndpoint = organization.getServiceIpAddress().concat(sb.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> pqPayload = new HttpEntity<>(headers);
        ResponseEntity<Void> response = networkService.put(priceQuotationEndpoint,pqPayload,Void.class);
        if(!response.getStatusCode().equals(HttpStatus.NO_CONTENT) && 
            !response.getStatusCode().equals(HttpStatus.CREATED)){
            throw new AesException("Something wrong");
        }
    }

    private void sentOfferDeclineRequest(String authToken, Organization organization,Offer offer, NoteDto noteDto){
        StringBuilder sb = new StringBuilder("/price-quotations");
                
            sb.append("/").append(offer.getId()).append("/decline/receive");
        
        String priceQuotationEndpoint = organization.getServiceIpAddress().concat(sb.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<NoteDto> pqPayload = new HttpEntity<>(noteDto,headers);
        ResponseEntity<Void> response = networkService.put(priceQuotationEndpoint,pqPayload,Void.class);
        if(!response.getStatusCode().equals(HttpStatus.NO_CONTENT) && 
            !response.getStatusCode().equals(HttpStatus.CREATED)){
            throw new AesException("Something wrong");
        }
    }

    

    
}
