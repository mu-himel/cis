package com.aes.erp.vendor.document_response_dto;

import lombok.Data;

@Data
public class DocumentHolderResponseDto {
    private Long id;
    private String documentHolderName;
    private String nidNumber;
    private String tinNumber;
    private String binNumber;
    private String tradeLicenseNumber;
    private String bankAccountNumber;
}
