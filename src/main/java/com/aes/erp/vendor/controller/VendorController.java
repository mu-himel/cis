package com.aes.erp.vendor.controller;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.service.VendorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
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
                vendorService.getVendorDetail(id),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<?> getVendors(
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("size") Optional<Integer> size,
            @RequestParam("searchFilter") Optional<String> searchFilter
    )
    {
        return new ResponseEntity<>(
                vendorService.getVendors(page,size,searchFilter),
                HttpStatus.OK
        );
    }

    // crate vendor
    @PostMapping
    public ResponseEntity<?> createVendor(@RequestBody VendorDto vendorDto) {
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

    @PostMapping("/upload/{id}")
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

        System.out.println(address);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/profile/{userId}")
    public ResponseEntity<VendorProfileDto> getVendorProfile(@PathVariable("userId") Long userId) {
        return  new ResponseEntity<VendorProfileDto>(vendorService.getVendorProfile(userId), HttpStatus.OK);
    }
    @PostMapping("/{id}/approve")
    public void approveVendorProfile(@PathVariable("id") Long id){
        vendorService.approveVendor(id);
    }
}
