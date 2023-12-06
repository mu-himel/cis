package com.aes.erp.vendor.service;

import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.dto.VendorProfileDto;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.enums.VendorStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

public interface VendorService {
    Optional<?> getVendorDetail(Long vendorId);
    void createVendor(VendorDto vendorDto);

    void updateVendor(Long id, VendorDto vendorDto);

    void deleteVendor(Long id);

    Page<?> getVendors(Optional<Integer> page, Optional<Integer> size, Optional<String> searchFilter);

    void uploadVendorFiles(Long id,
                           String address,
                           String password,
                           String confirmPassword,
                           Optional<MultipartFile> tinCert,
                           Optional<MultipartFile> vatCert,
                           Optional<MultipartFile> tradeLicense,
                           Optional<MultipartFile> quotationFormat);

    void updateVendorStatus(Long id, VendorStatus status);
    VendorProfileDto getVendorProfile(Long userId);
    void approveVendor(Long vendorId);
    Vendor getVendorByUserId(Long userId);
}
