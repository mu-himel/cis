package com.aes.erp.vendor.service;


import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.vendor.dto.VendorDetailsDto;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.dto.VendorScoreDto;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.entity.VendorFile;
import com.aes.erp.vendor.enums.VendorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface VendorService {
    Optional<?> getVendorDetail(Long vendorId);
    public void removeSubCategoryFromVendor(Long vendorId, Long subCategoryId);
    void createVendor(VendorDto vendorDto);

    void updateVendor(Long id, VendorDto vendorDto);

    void deleteVendor(Long id);

    Page<?> getVendors(Optional<Integer> page, Optional<Integer> size,
                       Optional<String> name,
                       Optional<String> email,
                       Optional<String> phone,
                       Optional<String> vendorType,
                       Optional<String> vendorStatus
    );
    Page<?> getPendingVerificationVendors(Optional<Integer> page, Optional<Integer> size,
                                          Optional<String> name,
                                          Optional<String> email,
                                          Optional<String> phone,
                                          Optional<String> vendorType,
                                          Optional<String> vendorStatus
                                          );
    Page<?> getPendingApprovalVendors(Optional<Integer> page, Optional<Integer> size,
                                      Optional<String> name,
                                      Optional<String> email,
                                      Optional<String> phone,
                                      Optional<String> vendorType,
                                      Optional<String> vendorStatus
                                      );

    void uploadVendorFiles(Long id,
                           String address,
                           String password,
                           String confirmPassword,
                           Optional<MultipartFile> tinCert,
                           Optional<MultipartFile> vatCert,
                           Optional<MultipartFile> tradeLicense,
                           Optional<MultipartFile> quotationFormat);

    FileUploadResponse uploadVendorFile(Long id,Optional<MultipartFile> file);

    void updateVendorStatus(Long id, VendorStatus status);
    VendorProfileDto getVendorProfile(Long userId);
    void approveVendor(Long vendorId,VendorScoreDto dto);
    Optional<Vendor> getVendorByUserId(Long userId);
    void updateVendorScore(VendorScoreDto dto);
    Vendor getById(Long id);


    Page<?> getApprovedVendors(Optional<Integer> page, Optional<Integer> size,
                               Optional<String> name,
                               Optional<String> email,
                               Optional<String> phone,
                               Optional<String> vendorType,
                               Optional<String> vendorStatus
                               );

    VendorDetailsDto getAllDetailsOfVendor(Long vendorId);

    Optional<VendorFile> getShopFile(Long id,  String filename);

    void rejectVendor(Long id);
}
