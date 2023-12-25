package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.Document;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class DocumentHolderResponseDto {
    private Long id;
    private String documentHolderName;
    private String nidNumber;
    private String tinNumber;
    private String binNumber;
    private String tradeLicenseNumber;
    private String bankAccountNumber;
    private String msg;
    private Set<Document> documentsList = new HashSet<>();
}
