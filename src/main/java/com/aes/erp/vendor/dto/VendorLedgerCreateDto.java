package com.aes.erp.vendor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class VendorLedgerCreateDto {
    private String vendorName;
    private String vendorEmail;
    private String vendorPhone;
    private String vendorType;
    private String vendorNid;
    private String tin;
    private String tradeLicense;
    private String vatCertificate;
    private String bankSolvency;
    private String categories;

    private String modeOfTransaction;
    private String creditPeriod;
    private String modeOfTransportation;
    private String avgDeliveryTime;
    private String deliverySchedule;
    private String urgentDeliverySupport;
    private String productReplacementType;
    private String annualBusinessVolume;
    private String vendorCpsId;

    private String vendorBankAccountNo;
    private String vendorBankName;
    private String vendorBankHolderName;
    private String vendorBankBranch;
    private String routingNumber;

    private List<BusinessDetailsDto> businessDetails;
}
