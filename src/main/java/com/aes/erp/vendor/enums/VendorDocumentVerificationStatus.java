package com.aes.erp.vendor.enums;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "Vendor Verify Status")
public enum VendorDocumentVerificationStatus {
    PENDING_DOCUMENT_VERIFICATION,
    DOCUMENTS_SUBMITTED,
    VERIFIED,
    PENDING_VERIFICATION, PENDING_APPROVAL,  APPROVED,
}