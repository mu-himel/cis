package com.aes.erp.vendor.service;

import com.aes.erp.vendor.dto.VendorAddressDto;
import com.aes.erp.vendor.dto.VendorBasicInformationDto;
import com.aes.erp.vendor.dto.VendorIdentificationDto;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.service.DocumentServices.DocumentService;
import org.springframework.stereotype.Service;

@Service
public class VendorProfileServiceImpl implements VendorProfileService{
    private final DocumentService documentService;

    public VendorProfileServiceImpl(DocumentService documentService) {
        this.documentService = documentService;
    }

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
            Document binDocument = documentService.getDocumentByDocumentHolderIdAndType(documentHolder.getId(), DocumentType.BIN);
            if(binDocument != null)identificationDto.setBinFile(binDocument.getFile());
            if(documentHolder.getBinDocument() != null)identificationDto.setBin(documentHolder.getBinDocument().getBin());

            Document nidDocument = documentService.getDocumentByDocumentHolderIdAndType(documentHolder.getId(), DocumentType.NID);
            if(nidDocument != null)identificationDto.setNidFile(nidDocument.getFile());
            if(documentHolder.getNidDocument() != null)identificationDto.setNid(documentHolder.getNidDocument().getNid());

            if(documentHolder.getTinDocument() != null)identificationDto.setTin(documentHolder.getTinDocument().getTin());
            Document tinDocument = documentService.getDocumentByDocumentHolderIdAndType(documentHolder.getId(), DocumentType.TIN);
            if(tinDocument != null)identificationDto.setTinFile(tinDocument.getFile());

            if(documentHolder.getBankSolvencyDocument() != null)identificationDto.setSolvency(documentHolder.getBankSolvencyDocument().getAccount());
            Document bankSolvencyDocument = documentService.getDocumentByDocumentHolderIdAndType(documentHolder.getId(), DocumentType.BANK_SOLVENCY);
            if(bankSolvencyDocument != null)identificationDto.setSolvencyFile(bankSolvencyDocument.getFile());

            if(documentHolder.getTradeDocument() != null)identificationDto.setTrade(documentHolder.getTradeDocument().getTradeLicenseNumber());
            Document tradeDocument = documentService.getDocumentByDocumentHolderIdAndType(documentHolder.getId(), DocumentType.TRADE);
            if(tradeDocument != null)identificationDto.setTradeFile(tradeDocument.getFile());
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
