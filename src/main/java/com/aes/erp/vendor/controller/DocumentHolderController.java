package com.aes.erp.vendor.controller;

import com.aes.erp.vendor.document_response_dto.*;
import com.aes.erp.vendor.service.DocumentHolderServices.DocumentHolderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vendors/documents-holder")
public class DocumentHolderController {
    private final DocumentHolderService documentHolderService;


    public DocumentHolderController(DocumentHolderService documentHolderService) {
        this.documentHolderService = documentHolderService;
    }

    @PostMapping("/{userId}")
    public ResponseEntity<DocumentHolderResponseDto> createDocumentHolder(@PathVariable("userId") Long userId, @RequestBody DocumentHolderRequestDto requestDto) {
        DocumentHolderResponseDto responseDto = documentHolderService.create(userId, requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<DocumentHolderResponseDto> getDocumentHolder(@PathVariable("id") Long id) {
        DocumentHolderResponseDto responseDto = documentHolderService.getDocumentHolderById(id);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }
    @PostMapping("/mismatch/{id}")
    public ResponseEntity<MisMatchResponseDto> getMisMatch(@PathVariable("id")Long id, @RequestBody MisMatchDto misMatchDto) {
        return new ResponseEntity<>(documentHolderService.findMisMatch(misMatchDto, id), HttpStatus.OK);
    }
    @PostMapping("{userId}/add-details/{id}")
    public ResponseEntity<?> addDocumentHolderDetails(@PathVariable("id")Long id,
                                                      @PathVariable("userId") Long userId,
                                                      @RequestBody DetailsDTO dto) {

        documentHolderService.addHolderDetails(dto, userId, id);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PutMapping("update-details/{id}")
    public ResponseEntity<?> updateHolderDetails(@PathVariable("id") Long id, @RequestBody DetailsDTO dto){
        documentHolderService.updateHolderDetails(dto, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("information/{id}")
    public ResponseEntity<?> getHolderInformation(@PathVariable("id") Long id){
        return new ResponseEntity<>(documentHolderService.getHolderExtractedDetailsForConfirmation(id), HttpStatus.OK);
    }
}
