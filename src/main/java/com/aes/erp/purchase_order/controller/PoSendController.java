package com.aes.erp.purchase_order.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aes.erp.purchase_order.dto.request.QcResultDto;
import com.aes.erp.purchase_order.service.PurchaseOrderService;
import com.aes.erp.scm.dto.NoteDto;

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

    @PutMapping("/{id}/received-grn")
    public ResponseEntity<?> receiveGrn(@PathVariable("id") Long id){
        purchaseOrderService.grnReceive(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/declined-grn")
    public ResponseEntity<?> declineGrn(@PathVariable("id") Long id,
    @RequestBody NoteDto noteDto
    ){
        purchaseOrderService.declineGrn(id,noteDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/receive-qc")
    public ResponseEntity<?> receiveQc(@PathVariable("id") Long id,
        @RequestBody QcResultDto qcResultDto){
        purchaseOrderService.receiveQc(id,qcResultDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PutMapping("/{id}/decline-qc")
    public ResponseEntity<?> declineQc(@PathVariable("id") Long id,
        @RequestBody QcResultDto qcResultDto){
        purchaseOrderService.declineQc(id,qcResultDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
