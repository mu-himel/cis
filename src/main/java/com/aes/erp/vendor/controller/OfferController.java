package com.aes.erp.vendor.controller;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.service.offer_services.OfferService;
import com.aes.erp.vendor.service.offer_services.OfferServiceImpl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/api/v1/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferServiceImpl offerService) {
        this.offerService = offerService;
    }

    @PostMapping("/initial-offer/{tenderId}")
    public ResponseEntity<?> acceptTender(
            @RequestAttribute ClaimResponseDto loggedInUser,
            @PathVariable("tenderId") Long tenderId,
                                          @RequestBody OfferCreateDTO offerCreateDTO){
        offerService.createInitialOffer(loggedInUser, offerCreateDTO, tenderId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/counter-offer/{tenderId}")
    public ResponseEntity<?> createCounterOffer(
        @RequestAttribute ClaimResponseDto loggedInUser,    
        @PathVariable("tenderId") Long tenderId,
                                                @RequestBody OfferCreateDTO offerCreateDTO){
        offerService.createCounterOffer(loggedInUser, offerCreateDTO, tenderId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/counter-offer/{tenderId}/receive")
    public ResponseEntity<?> receiveCounterOffer(@PathVariable("tenderId") Long tenderId,
                                                 @RequestBody OfferCreateDTO offerCreateDTO){

        Long id = offerService.receiveCounterOffer(offerCreateDTO, tenderId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("id", id.toString());
        return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOfferById(@PathVariable("id") Long id){
        return new ResponseEntity<>(offerService.getById(id).orElse(null) ,HttpStatus.OK);
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<?> lockOfferByVendor(
        @PathVariable("id") Long id, 
        @RequestAttribute ClaimResponseDto loggedInUser
    ){
        offerService.lockOffer(loggedInUser, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/lock/{vendorId}")
    public ResponseEntity<?> declineOfferFromOrganization(
        @PathVariable("id") Long id, 
        @PathVariable("vendorId") Long vendorId
    ){
        offerService.declineOffer(id,vendorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/decline/{vendorId}")
    public ResponseEntity<?> lockOfferFromOrganization(
        @PathVariable("id") Long id, 
        @PathVariable("vendorId") Long vendorId
    ){
        offerService.lockOffer(id,vendorId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
