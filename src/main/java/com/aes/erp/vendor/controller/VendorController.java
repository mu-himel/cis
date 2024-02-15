package com.aes.erp.vendor.controller;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.dto.VendorApprovalResponseDto;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.dto.VendorScoreDto;
import com.aes.erp.vendor.entity.VendorFile;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/vendors")
public class VendorController {

    @Autowired
    private VendorService vendorService;



    // get vendor info
    @GetMapping("/{id}")
    public ResponseEntity<?> getVendor(@PathVariable("id") Long id) {
        return new ResponseEntity<>(
                vendorService.getVendorDetail(id).get(),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<?> getVendors(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("phone") Optional<String> phone,
            @RequestParam("vendorType") Optional<String> vendorType,
            @RequestParam("vendorStatus") Optional<String> vendorStatus
    )
    {
        return new ResponseEntity<>(
                vendorService.getVendors(page,size,name,email,phone,vendorType,vendorStatus),
                HttpStatus.OK
        );
    }

    @GetMapping("/pending-verification")
    public ResponseEntity<?> getPendingVerificationVendors(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("phone") Optional<String> phone,
            @RequestParam("vendorType") Optional<String> vendorType,
            @RequestParam("vendorStatus") Optional<String> vendorStatus
    )
    {
        return new ResponseEntity<>(
            vendorService.getPendingVerificationVendors(page,size,
            name,email,phone,vendorType,vendorStatus),
            HttpStatus.OK
        );
    }

    @GetMapping("/pending-approval")
    public ResponseEntity<?> getPendingApprovalVendors(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("phone") Optional<String> phone,
            @RequestParam("vendorType") Optional<String> vendorType,
            @RequestParam("vendorStatus") Optional<String> vendorStatus
    )
    {
        return new ResponseEntity<>(
                vendorService.getPendingApprovalVendors(page,size,
                        name,email,phone,vendorType,vendorStatus
                ),
                HttpStatus.OK
        );
    }

    @GetMapping("/complete")
    public ResponseEntity<?> getApprovedVendors(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("name") Optional<String> name,
            @RequestParam("email") Optional<String> email,
            @RequestParam("phone") Optional<String> phone,
            @RequestParam("vendorType") Optional<String> vendorType,
            @RequestParam("vendorStatus") Optional<String> vendorStatus
    ){
        return new ResponseEntity<>(
                vendorService.getApprovedVendors(page,size,name,email,phone,vendorType,vendorStatus),
                HttpStatus.OK
        );
    }



    @PostMapping
    public ResponseEntity<?> createVendor(@RequestBody VendorDto vendorDto){
        vendorService.createVendor(vendorDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVendor(@PathVariable("id") Long id, @RequestBody VendorDto vendorDto){
        vendorService.updateVendor(id, vendorDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{id}/enabled")
    public ResponseEntity<?> updateVendorStatusToEnabled(@PathVariable("id") Long id){
        vendorService.updateVendorStatus(id, VendorStatus.ENABLED);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PutMapping("/{id}/disabled")
    public ResponseEntity<?> updateVendorStatusToDisabled(@PathVariable("id") Long id){
        vendorService.updateVendorStatus(id, VendorStatus.DISABLED);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteVendor(@PathVariable("id") Long id){
        vendorService.deleteVendor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/uploads/{id}")
    public ResponseEntity<?> uploadFiles(
            @PathVariable("id") Long id,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            @RequestParam("tinCert") Optional<MultipartFile> tinCert,
            @RequestParam("vatCert") Optional<MultipartFile> vatCert,
            @RequestParam("tradeLicense") Optional<MultipartFile> tradeLicense,
            @RequestParam("quotationFormat") Optional<MultipartFile> quotationFormat
            ){

        if(!password.equals(confirmPassword)){
            throw new AesException("Password and Confirm password not same");
        }

        vendorService.uploadVendorFiles(id,address,password,confirmPassword,tinCert,
                vatCert,tradeLicense,quotationFormat);

//        System.out.println(address);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/upload/{id}")
    public ResponseEntity<?> uploadFile(
            @PathVariable("id") Long id,
            @RequestPart("file") Optional<MultipartFile> file
    ){
        return new ResponseEntity<>(
                vendorService.uploadVendorFile(id,file),
                HttpStatus.OK
        );
    }


    private ByteArrayResource load(Long id, String filename) {
            Optional<VendorFile> vendorFileOp = vendorService.getShopFile(id,filename);
            if(vendorFileOp.isEmpty()){
                return null;
            }
            try {
                VendorFile vendorFile = vendorFileOp.get();
                Path path = Path.of(vendorFile.getPath(),vendorFile.getFileName());
                ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                if (resource.exists() || resource.isReadable()) {
                    return resource;
                } else {
                    throw new RuntimeException("Could not read the file!");
                }
            } catch (IOException e) {
                throw new RuntimeException("Error: " + e.getMessage());
            }
    }

    @GetMapping(value = "/images/{id}/{businessDetailId}/{filename:.+}",produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<?> getImage(
            @PathVariable("id") Long id,
            @PathVariable("businessDetailId") Long businessDetailId,
            @PathVariable String filename) {
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(
                    load(id,filename)
                );

    }


    @GetMapping("/profile/{id}")
    public ResponseEntity<VendorProfileDto> getVendorProfile(@PathVariable() Long id) {
        return  new ResponseEntity<>(vendorService.getVendorProfile(id), HttpStatus.OK);
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<VendorApprovalResponseDto> approveVendorProfile(@PathVariable("id") Long id, @RequestBody VendorScoreDto dto){
        vendorService.updateVendorScore(dto);
        vendorService.approveVendor(id,dto);
        VendorApprovalResponseDto approvalDto = new VendorApprovalResponseDto();


        approvalDto.setMessage("Approved");
        return new ResponseEntity<>(approvalDto, HttpStatus.OK);
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectVendor(@PathVariable("id") Long id){
        vendorService.rejectVendor(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<?> getAllDetailsOfVendor(@PathVariable("id") Long id){
        return new ResponseEntity<>(vendorService.getAllDetailsOfVendor(id), HttpStatus.OK);
    }
    @DeleteMapping("/{vendorId}/subcategory/{subCategoryId}")
    public ResponseEntity<?> removeSubCategoryForVendor(@PathVariable("vendorId") Long vendorId, @PathVariable("subCategoryId") Long subCategoryId){
        vendorService.removeSubCategoryFromVendor(vendorId, subCategoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/count/{subCatCode}")
    public ResponseEntity<?> getAvailableVendorsBySubCategory(@PathVariable String subCatCode){
        return new ResponseEntity<>(
                vendorService.getAvailableVendorCountBySubCategory(subCatCode).orElse(null),
                HttpStatus.OK
        );
    }
}
