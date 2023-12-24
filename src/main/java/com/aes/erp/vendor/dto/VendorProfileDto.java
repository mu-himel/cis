package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import com.aes.erp.vendor.entity.VendorFile;
import com.aes.erp.vendor.entity.VendorScore;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class VendorProfileDto {
    private String name;
    private Date startedAt;
    private VendorIdentificationDto identification;
    private VendorBasicInformationDto basicInformation;
    private VendorAddressDto address;
    private List<String> permittedProducts = new ArrayList<>();
    private GeneralDetails generalDetails;
    private List<BusinessDetails> businessDetails;
    private List<VendorFile> vendorFileList;
    private VendorScore vendorScore;
}