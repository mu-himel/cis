package com.aes.erp.vendor.service;

import com.aes.erp.vendor.dto.VendorAddressDto;
import com.aes.erp.vendor.dto.VendorBasicInformationDto;
import com.aes.erp.vendor.dto.VendorIdentificationDto;
import com.aes.erp.vendor.entity.Vendor;

public interface VendorProfileService {
    VendorBasicInformationDto getVendorBasicInformation(Vendor vendor);
    VendorIdentificationDto getVendorIdentification(Vendor vendor);
    VendorAddressDto getVendorAddress(Vendor vendor);
}
