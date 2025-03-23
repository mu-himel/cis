package com.aes.erp.scm.Controller;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.scm.Entities.TenderType;
import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.scm.dto.TenderCreateDto;
import com.aes.erp.scm.services.TenderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/tenders")
public class TenderController {
    private final TenderService tenderService;

    public TenderController(TenderService tenderService) {
        this.tenderService = tenderService;
    }
    @PostMapping()
    public ResponseEntity<?> createTender(@RequestBody TenderCreateDto tenderDto) {
        tenderService.createTender(tenderDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("/tender-entity/page")
    public ResponseEntity<?> getAllTenders(
            @RequestAttribute ClaimResponseDto loggedInUser,
            @RequestParam("searchFilter") Optional<String> searchFilter,
            @RequestParam("startDate") Optional<Long> startDate,
            @RequestParam("endDate") Optional<Long> endDate,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("tenderType") Optional<TenderType> tenderType) {
        return new ResponseEntity<>(tenderService.getAllTenders(loggedInUser, searchFilter, page, size, tenderType, startDate, endDate),
                HttpStatus.OK);
    }

    @GetMapping("/lowest/{rfqNo}")
    public ResponseEntity<?> getTenderLowestBids(@PathVariable("rfqNo") String rfqNo) {
        return new ResponseEntity<>(tenderService.getTenderLowestBids(rfqNo), HttpStatus.OK);
    }

    @Deprecated(since = "newdev=0.0.15", forRemoval = true)
    @GetMapping("/tender-projection/page/filter")
    public ResponseEntity<?> getTenderProjection(
            @RequestAttribute ClaimResponseDto loggedInUser, @RequestParam("searchFilter") Optional<String> searchFilter,
            @RequestParam("startDate") Optional<Long> startDate,
            @RequestParam("endDate") Optional<Long> endDate,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("tenderType") Optional<TenderType> tenderType) {
        return new ResponseEntity<>(tenderService.getAllTenderProjection(loggedInUser, searchFilter, page, size, tenderType, startDate, endDate),
                HttpStatus.OK);
    }

    @GetMapping("/tender-projection/page")
    public ResponseEntity<?> getTenderProjectionWithFilter(
        @RequestAttribute ClaimResponseDto loggedInUser,@RequestParam("searchFilter") Optional<String> searchFilter,
                                           @RequestParam("fromDate") Optional<String> fromDate,
                                           @RequestParam("toDate")  Optional<String> toDate ,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size,
                                           @RequestParam("tenderType") Optional<TenderType> tenderType,
                                           @RequestParam("itemQty") Optional<Long> itemQty,
                                           @RequestParam("organizationId") Optional<Long> organizationId,
                                           @RequestParam("categoryId") Optional<Long> categoryId){
        return new ResponseEntity<>(tenderService.getAllTenderProjectionWithFilter(loggedInUser, searchFilter, page, size, tenderType,itemQty,organizationId,categoryId, fromDate, toDate),
                HttpStatus.OK);
    }

    @GetMapping("/closed")
    public ResponseEntity<?> getClosedTenders(
            @RequestAttribute ClaimResponseDto loggedInUser,@RequestParam("searchFilter") Optional<String> searchFilter,
            @RequestParam("fromDate") Optional<String> fromDate,
            @RequestParam("toDate")  Optional<String> toDate ,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("tenderType") Optional<TenderType> tenderType,
            @RequestParam("tenderNo") Optional<String> tenderNo,
            @RequestParam("organizationId") Optional<Long> organizationId,
            @RequestParam("categoryId") Optional<Long> categoryId){
        return new ResponseEntity<>(tenderService.getClosedTenderProjection(
                loggedInUser, searchFilter, page, size,
                tenderType, organizationId, categoryId,
                tenderNo, fromDate, toDate),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTenderById(@PathVariable("id") Long id){
        return new ResponseEntity<>(tenderService.getTenderById(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/negotiation-history")
    public ResponseEntity<?> getNegotiationHistory(
        @RequestAttribute ClaimResponseDto loggedInUser,
        @PathVariable("id") Long id){
        return new ResponseEntity<>(
            tenderService.getNegotiationHistories(loggedInUser, id),
            HttpStatus.OK
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectTender(
        @RequestAttribute ClaimResponseDto loggedInUser,
        @PathVariable("id") Long id,
        @RequestBody NoteDto noteDto
        ){
            tenderService.rejectTender(loggedInUser,id, noteDto);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/expire/{rfqNo}")
    public ResponseEntity<?> expireTender(@PathVariable("rfqNo") String rfqNo){
        tenderService.expire(rfqNo);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
