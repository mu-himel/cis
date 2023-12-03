package com.aes.erp.vendor.service;

import com.aes.erp.vendor.dto.VendorAddressDto;
import com.aes.erp.vendor.dto.VendorBasicInformationDto;
import com.aes.erp.vendor.dto.VendorIdentificationDto;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import org.springframework.stereotype.Service;

@Service
public class VendorProfileServiceImpl implements VendorProfileService{
    @Override
    public VendorBasicInformationDto getVendorBasicInformation(Vendor vendor) {
        VendorBasicInformationDto basicInfo = new VendorBasicInformationDto();
        if(vendor.getName() != null && !vendor.getName().isEmpty())basicInfo.setName(vendor.getName());
        if(vendor.getEmail() != null && !vendor.getEmail().isEmpty())basicInfo.setEmail(vendor.getEmail());
        if(vendor.getPhone() != null && !vendor.getPhone().isEmpty())basicInfo.setPhone(vendor.getPhone());
        return basicInfo;
    }

    @Override
    public VendorIdentificationDto getVendorIdentification(Vendor vendor) {
        VendorIdentificationDto identificationDto = new VendorIdentificationDto();
        if(vendor.getDocumentHolder() != null){
            DocumentHolder documentHolder = vendor.getDocumentHolder();
            identificationDto.setBin(documentHolder.getBinDocument().getBin());
            identificationDto.setNid(documentHolder.getNidDocument().getNid());
            identificationDto.setTin(documentHolder.getTinDocument().getTin());
            identificationDto.setSolvency(documentHolder.getBankSolvencyDocument().getAccount());
            identificationDto.setTrade(documentHolder.getTradeDocument().getTradeLicenseNumber());
        }
        return identificationDto;
    }

    @Override
    public VendorAddressDto getVendorAddress(Vendor vendor) {
        VendorAddressDto dto = new VendorAddressDto();
        if(vendor.getDocumentHolder() != null){
            DocumentHolder documentHolder = vendor.getDocumentHolder();
            if(documentHolder.getTinDocument() != null){
                dto.setPermanentAddress(documentHolder.getTinDocument().getPermanentAddress());
                dto.setPresentAddress(documentHolder.getTinDocument().getCurrentAddress());
            }
        }
        return dto;
    }
}
