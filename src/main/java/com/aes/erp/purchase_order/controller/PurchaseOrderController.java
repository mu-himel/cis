package com.aes.erp.purchase_order.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.purchase_order.dto.request.PoReceiveRequestDto;
import com.aes.erp.purchase_order.dto.request.PoRequestDto;
import com.aes.erp.purchase_order.repository.PoRepository.PurchaseOrderInfo;
import com.aes.erp.purchase_order.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/v1/po")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService poService;

    @PostMapping
    public ResponseEntity<?> receivePO(
        @RequestAttribute Organization organization,
        @RequestBody PoReceiveRequestDto poDto){
        poService.receivePO(organization, poDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/pending")
    public ResponseEntity<?> getPendingPOs(
            @RequestAttribute ClaimResponseDto loggedInUser,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("poNo") Optional<String> poNo,
            @RequestParam("tenderNo") Optional<String> tenderNo,
            @RequestParam("organizationId") Optional<Long> organizationId,
            @RequestParam("categoryId") Optional<String> categoryId,
            @RequestParam("subCategoryId") Optional<String> subCategoryId,
            @RequestParam("deliveryDate") Optional<String> deliveryDate,
            @RequestParam("status") Optional<String> status,
            @RequestParam("fromDate") Optional<String> fromDate,
            @RequestParam("toDate") Optional<String> toDate
    ){
        return new ResponseEntity<>(poService.getPendingPOs(loggedInUser, page, size, poNo, tenderNo, organizationId, categoryId, subCategoryId, deliveryDate, status, fromDate, toDate), HttpStatus.OK);
    }

    @GetMapping("/closed")
    public ResponseEntity<?> getClosedPOs(
            @RequestAttribute ClaimResponseDto loggedInUser,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("poNo") Optional<String> poNo,
            @RequestParam("tenderNo") Optional<String> tenderNo,
            @RequestParam("organizationId") Optional<Long> organizationId,
            @RequestParam("categoryId") Optional<String> categoryId,
            @RequestParam("subCategoryId") Optional<String> subCategoryId,
            @RequestParam("deliveryDate") Optional<String> deliveryDate,
            @RequestParam("fromDate") Optional<String> fromDate,
            @RequestParam("toDate") Optional<String> toDate

    ){
        return new ResponseEntity<>(
                poService.getClosedPOs(loggedInUser, page, size, poNo, tenderNo, organizationId, categoryId, subCategoryId, deliveryDate, fromDate, toDate),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailById(@RequestAttribute ClaimResponseDto loggedInUser, @PathVariable("id") Long id){
        return new ResponseEntity<>(poService.getDetailById(loggedInUser, id,Map.class).orElse(null),
            HttpStatus.OK
        );
    }

    @PutMapping("/{id}/upload-invoice")
    public ResponseEntity<?> uploadInvoice(
        @RequestAttribute ClaimResponseDto loggedInUser,
        @PathVariable("id") Long id,
        @RequestPart("file") Optional<MultipartFile> fileOp
    ){
        poService.uploadInvoice(loggedInUser,id, fileOp);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
}