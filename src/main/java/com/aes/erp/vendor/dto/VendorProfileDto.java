package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class VendorProfileDto {
    private String name;
    private Date startedAt;
    private VendorIdentificationDto identification;
    private VendorBasicInformationDto basicInformation;
    private VendorAddressDto address;
//    private List<String> permittedProducts;
    private GeneralDetails generalDetails;
    private BusinessDetails businessDetails;
}