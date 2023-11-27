package com.aes.erp.verification.controller;

import com.aes.erp.demand.service.DemandService;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.verification.dto.request.ApproveDto;
import com.aes.erp.verification.dto.request.VerifyDto;
import com.aes.erp.verification.enums.DomainType;
import com.aes.erp.verification.service.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/verifiers")
public class VerifierController {

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private DemandService demandService;

    @GetMapping
    public ResponseEntity<?> getVerifiers(
            @RequestHeader("Authorization") String token,
            @RequestHeader("uri") String uri,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("subCategoryId") Optional<Long> subCategoryId
    ){

        return new ResponseEntity<>(
                verificationService.getVerifiers(token, uri, categoryId, subCategoryId.orElse(null)),
                HttpStatus.OK);
    }

    @PutMapping("/review")
    public ResponseEntity<?> review(@RequestBody VerifyDto verifyDto){
        if(verifyDto.getDomainType()==DomainType.DEMAND){
            verificationService.setVerificationDomainService(demandService);
        }
        verificationService.review(verifyDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/verify")
    public ResponseEntity<?> verify(@RequestBody VerifyDto verifyDto){
        if(verifyDto.getDomainType()==DomainType.DEMAND){
            verificationService.setVerificationDomainService(demandService);
        }
        verificationService.verify(verifyDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/approve")
    public ResponseEntity<?> approve(@RequestBody ApproveDto approveDto){
        if(approveDto.getDomainType()==DomainType.DEMAND){
            verificationService.setVerificationDomainService(demandService);
        }
        verificationService.approve(approveDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
