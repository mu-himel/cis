package com.aes.erp.vendor.controller;

import com.aes.erp.vendor.service.negotiation_history.NegotiationHistoryService;
import com.aes.erp.vendor.service.offer_services.OfferService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/negotiation-history")
public class NegotiationHistoryController {
    private final NegotiationHistoryService negotiationHistoryService;
    private final OfferService offerService;

    public NegotiationHistoryController(NegotiationHistoryService negotiationHistoryService, OfferService offerService) {
        this.negotiationHistoryService = negotiationHistoryService;
        this.offerService = offerService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNegotiationHistory(@PathVariable("id") Long id){
        return new ResponseEntity<>(negotiationHistoryService.getHistoryById(id), HttpStatus.OK);
    }
    @GetMapping("/offers/{historyId}")
    public ResponseEntity<?> getAllOffers(@PathVariable("historyId") Long historyId){
        return new ResponseEntity<>(offerService.getAllOffersByNegotiationHistoryId(historyId), HttpStatus.OK);
    }
}
