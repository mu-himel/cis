package com.aes.erp.authentication.dto;

import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.aes.erp.vendor.enums.VendorStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorInfoDto extends UserInfoDto{
    private Long vendorId;
    private VendorStatus vendorStatus;
    private VendorDocumentVerificationStatus vendorDocumentVerificationStatus;
}
