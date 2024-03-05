package com.aes.erp.scm.Controller;

import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.scm.dto.TenderCreateDto;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.scm.Entities.TenderType;
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
                                           @RequestParam("endDate")  Optional<Long> endDate ,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size,
                                           @RequestParam("tenderType") Optional<TenderType> tenderType){
        return new ResponseEntity<>(tenderService.getAllTenders(loggedInUser, searchFilter, page, size, tenderType, startDate, endDate),
                HttpStatus.OK);
    }
    @GetMapping("/tender-projection/page")
    public ResponseEntity<?> getTenderProjection(
        @RequestAttribute ClaimResponseDto loggedInUser,@RequestParam("searchFilter") Optional<String> searchFilter,
                                           @RequestParam("startDate") Optional<Long> startDate,
                                           @RequestParam("endDate")  Optional<Long> endDate ,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size,
                                           @RequestParam("tenderType") Optional<TenderType> tenderType){
        return new ResponseEntity<>(tenderService.getAllTenderProjection(loggedInUser, searchFilter, page, size, tenderType, startDate, endDate),
                HttpStatus.OK);
    }

    @GetMapping("/closed")
    public ResponseEntity<?> getClosedTenders(
            @RequestAttribute ClaimResponseDto loggedInUser,@RequestParam("searchFilter") Optional<String> searchFilter,
            @RequestParam("startDate") Optional<Long> startDate,
            @RequestParam("endDate")  Optional<Long> endDate ,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("tenderType") Optional<TenderType> tenderType){
        return new ResponseEntity<>(tenderService.getClosedTenderProjection(loggedInUser, searchFilter, page, size, tenderType, startDate, endDate),
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
}
