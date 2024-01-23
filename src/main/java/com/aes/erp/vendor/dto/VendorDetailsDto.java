package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VendorDetailsDto {
    private GeneralDetails generalDetails;
    private List<BusinessDetails> businessDetails;
    private String vendorName;
    private String vendorType;
    private String vendorCategory;
    private List<String> subCategories = new ArrayList<>();
    private String nidNumber;
    private String binNumber;
    private String tinNumber;
    private String tradeNumber;
    private String solvencyNumber;
}
