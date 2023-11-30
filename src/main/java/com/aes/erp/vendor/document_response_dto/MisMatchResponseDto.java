package com.aes.erp.vendor.document_response_dto;

import lombok.Data;

import java.util.Map;

@Data
public class MisMatchResponseDto {
    private Map<String, String> nid;
    private Map<String, String> fullName;
    private Map<String, String> fatherName;
    private Map<String, String> address;
}
