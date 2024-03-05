package com.aes.erp.purchase_order.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.purchase_order.dto.request.PoRequestDto;
import com.aes.erp.purchase_order.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/v1/po")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService poService;

    @PostMapping
    public ResponseEntity<?> receivePO(
        @RequestAttribute ClaimResponseDto loggedInUser,
        @RequestBody PoRequestDto poDto){
        poService.receivePO(loggedInUser, poDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingPOs(
        @RequestAttribute ClaimResponseDto loggedInUser,
        @RequestParam("page") Optional<Integer> page,
        @RequestParam("size") Optional<Integer> size
    ){
        return new ResponseEntity<>(poService.getPendingPOs(loggedInUser, page,size),HttpStatus.OK);
    }
    
}