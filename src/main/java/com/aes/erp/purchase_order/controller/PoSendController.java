package com.aes.erp.purchase_order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.purchase_order.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/v1/po-send")
public class PoSendController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;
    
    @PutMapping("/{id}")
    public ResponseEntity<?> sendPoToErp(@PathVariable("id") Long id){
        purchaseOrderService.sendPO(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
