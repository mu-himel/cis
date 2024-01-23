package com.aes.erp.vendor.controller;
import com.aes.erp.vendor.document_response_dto.ConfirmDocumentDto;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolderStatus;
import com.aes.erp.vendor.service.DocumentHolderServices.DocumentHolderService;
import com.aes.erp.vendor.service.DocumentServices.*;
import com.aes.erp.vendor.service.VendorDocumentValidationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/vendors/documents")
public class VendorDocumentVerificationController {
    private final VendorDocumentValidationService vendorDocumentValidationService;
    private final BankSolvencyService bankSolvencyService;
    private final NIDService nidService;
    private final TINService tinService;
    private final BinService binService;
    private final TradeLicenseService tradeLicenseService;
    private final DocumentHolderService documentHolderService;

    public VendorDocumentVerificationController(VendorDocumentValidationService vendorDocumentValidationService, BankSolvencyService bankSolvencyService, NIDService nidService, TINService tinService, BinService binService, TradeLicenseService tradeLicenseService, DocumentHolderService documentHolderService) {
        this.vendorDocumentValidationService = vendorDocumentValidationService;
        this.bankSolvencyService = bankSolvencyService;
        this.nidService = nidService;
        this.tinService = tinService;
        this.binService = binService;
        this.tradeLicenseService = tradeLicenseService;
        this.documentHolderService = documentHolderService;
    }

    @PostMapping("/upload-pdf/{id}")
    public ResponseEntity<Object> uploadPdf(@PathVariable("id") Long documentHolderId,
                                            @RequestPart("file") MultipartFile file,
                                            @RequestParam("fileName")String fileName,
                                            @RequestParam("orgName") String orgName) {
        Object result = vendorDocumentValidationService.validateDocument(documentHolderId, fileName, file, orgName);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    @PostMapping("/confirmation/{id}")
    public ResponseEntity<String> confirmDocumentInformation(@PathVariable("id") Long documentHolderId, @RequestBody ConfirmDocumentDto confirmDocumentDto){
        bankSolvencyService.update(documentHolderId, confirmDocumentDto.getBankSolvency());
        nidService.update(documentHolderId, confirmDocumentDto.getNid());
        tinService.update(documentHolderId, confirmDocumentDto.getTin());
        binService.update(documentHolderId, confirmDocumentDto.getBin());
        tradeLicenseService.update(documentHolderId, confirmDocumentDto.getTrade());
        documentHolderService.updateDocumentHolderStatus(documentHolderId, DocumentHolderStatus.DOCUMENTS_SUBMITTED);
        return new ResponseEntity<>("Successful request", HttpStatus.NO_CONTENT);
    }
}
