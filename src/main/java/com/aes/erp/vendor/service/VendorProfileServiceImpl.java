package com.aes.erp.vendor.service;

import com.aes.erp.authentication.AuthenticateController;
import com.aes.erp.vendor.dto.VendorAddressDto;
import com.aes.erp.vendor.dto.VendorBasicInformationDto;
import com.aes.erp.vendor.dto.VendorIdentificationDto;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.service.DocumentServices.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class VendorProfileServiceImpl implements VendorProfileService{
    private final DocumentService documentService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateController.class);

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
            Document binDocument = documentHolder.getBinDocument().getDocument();
            if(binDocument != null){
                // identificationDto.setBinFile(binDocument.getFile());
                identificationDto.setBinFilePath(binDocument.getFilePath().substring(1)+"/"+binDocument.getFileName());
                identificationDto.setBinContentType(binDocument.getContentType());
                identificationDto.setBin(documentHolder.getBinDocument().getBin());
            }
            Document nidDocument = documentHolder.getNidDocument().getDocument();
            if(nidDocument != null){
                // identificationDto.setNidFile(nidDocument.getFile());
                if(nidDocument.getFilePath()!=null)
                    identificationDto.setNidFilePath(nidDocument.getFilePath().substring(1)+"/"+nidDocument.getFileName());
                identificationDto.setNidContentType(nidDocument.getContentType());
                identificationDto.setNid(documentHolder.getNidDocument().getNid());
                identificationDto.setOwnerName(documentHolder.getNidDocument().getName());
            }
            Document tinDocument = documentHolder.getTinDocument().getDocument();
            if(tinDocument != null){
                identificationDto.setTin(documentHolder.getTinDocument().getTin());
                if(tinDocument.getFilePath()!=null)
                    identificationDto.setTinFilePath(tinDocument.getFilePath().substring(1)+"/"+tinDocument.getFileName());
                // identificationDto.setTinFile(tinDocument.getFile());
                identificationDto.setTinContentType(tinDocument.getContentType());
            }
            Document bankSolvencyDocument = documentHolder.getBankSolvencyDocument().getDocument();
            if(bankSolvencyDocument != null){
                identificationDto.setSolvency(documentHolder.getBankSolvencyDocument().getAccount());
                if(bankSolvencyDocument.getFilePath()!=null)
                    identificationDto.setSolvencyFilePath(bankSolvencyDocument.getFilePath().substring(1)+"/"+bankSolvencyDocument.getFileName());
                // identificationDto.setSolvencyFile(bankSolvencyDocument.getFile());
                identificationDto.setSolvencyContentType(bankSolvencyDocument.getContentType());
            }
            Document tradeDocument = documentHolder.getTradeDocument().getDocument();
            if(tradeDocument != null){
                // identificationDto.setTradeFile(tradeDocument.getFile());
                if(tradeDocument.getFilePath()!=null){
                    identificationDto.setTradeFilePath(tradeDocument.getFilePath().substring(1)+"/"+tradeDocument.getFileName());
                }
                
                identificationDto.setTradeContentType(tradeDocument.getContentType());
                identificationDto.setTrade(documentHolder.getTradeDocument().getTradeLicenseNumber());
            }
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
