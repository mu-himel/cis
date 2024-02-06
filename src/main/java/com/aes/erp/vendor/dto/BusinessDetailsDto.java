package com.aes.erp.vendor.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BusinessDetailsDto {
    private Long id;
    private String orgName;
    private String businessType;
    private String numberOfYear;
    private String annualVolume;
    private String workOrderFile;
}
