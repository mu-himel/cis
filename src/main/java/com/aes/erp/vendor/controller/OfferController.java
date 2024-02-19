package com.aes.erp.vendor.controller;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.vendor.dto.OfferCreateDTO;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.aes.erp.vendor.service.offer_services.OfferService;
import com.aes.erp.vendor.service.offer_services.OfferServiceImpl;
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

        offerService.receiveCounterOffer(offerCreateDTO, tenderId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Offer> getOfferById(@PathVariable("id") Long id){
        return new ResponseEntity<>(offerService.getById(id) ,HttpStatus.OK);
    }
}
