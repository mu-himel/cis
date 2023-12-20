package com.aes.erp.vendor.controller;

import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.dto.VendorScoreDto;
import com.aes.erp.vendor.entity.VendorScore;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.service.VendorService;
import com.fasterxml.jackson.core.JsonProcessingException;
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
            @RequestParam("employeeType") EmployeeType employeeType,
            @RequestParam("file") Optional<MultipartFile> file
    ){
        vendorService.uploadVendorFile(id,employeeType,file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @GetMapping("/profile/{id}")
    public ResponseEntity<VendorProfileDto> getVendorProfile(@PathVariable() Long id) {
        return  new ResponseEntity<>(vendorService.getVendorProfile(id), HttpStatus.OK);
    }
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveVendorProfile(@PathVariable("id") Long id, @RequestBody VendorScoreDto dto){
        vendorService.updateVendorScore(dto);
        vendorService.approveVendor(id);
        return new ResponseEntity<>("Approved", HttpStatus.OK);
    }
}
