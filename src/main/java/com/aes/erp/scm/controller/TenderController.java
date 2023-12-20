package com.aes.erp.scm.controller;

import com.aes.erp.inventory.service.OrganizationService;
import com.aes.erp.scm.DtoCollection.TenderCreateDto;
import com.aes.erp.scm.Entities.TenderType;
import com.aes.erp.scm.services.TenderService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    @GetMapping("/list")
    public ResponseEntity<?> getAllTenders(@RequestParam("searchFilter") Optional<String> searchFilter,
                                           @RequestParam("startDate") Optional<Long> startDate,
                                           @RequestParam("endDate")  Optional<Long> endDate ,
                                           @RequestParam("page") Optional<Integer> page,
                                           @RequestParam("size") Optional<Integer> size,
                                           @RequestParam("tenderType") Optional<TenderType> tenderType){
        return new ResponseEntity<>(tenderService.getAllTenders(searchFilter, page, size, tenderType, startDate, endDate),
                HttpStatus.OK);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getTenderById(@PathVariable("id") Long id){
        return new ResponseEntity<>(tenderService.getTenderById(id), HttpStatus.OK);
    }
}
