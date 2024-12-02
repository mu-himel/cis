package com.aes.erp.purchase_order.controller;

import com.aes.erp.inventory.entity.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> receiveGrn(
            @RequestAttribute("organization") Organization organization,
            @PathVariable("id") String id) {
        purchaseOrderService.grnReceive(organization, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/declined-grn")
    public ResponseEntity<?> declineGrn(
            @RequestAttribute("organization") Organization organization,
            @PathVariable("id") String id,
            @RequestBody NoteDto noteDto
    ) {
        purchaseOrderService.declineGrn(organization, id, noteDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/receive-qc")
    public ResponseEntity<?> receiveQc(
            @RequestAttribute("organization") Organization organization,
            @PathVariable("id") String id,
            @RequestBody QcResultDto qcResultDto) {
        purchaseOrderService.receiveQc(organization, id, qcResultDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/decline-qc")
    public ResponseEntity<?> declineQc(@RequestAttribute("organization") Organization organization,
                                       @PathVariable("id") String id,
                                       @RequestBody QcResultDto qcResultDto) {
        purchaseOrderService.declineQc(organization, id, qcResultDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
