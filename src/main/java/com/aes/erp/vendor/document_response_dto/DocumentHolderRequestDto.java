package com.aes.erp.vendor.document_response_dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DocumentHolderRequestDto {
    private Boolean nid = false;
    private Boolean tin = false;
    private Boolean bin = false;
    private Boolean bankSolvency = false;
    private Boolean tradeLicense = false;
    private Boolean resume = false;
    private Boolean certificateOfIncorporation = false;
    private Boolean memorandumOfAssociation = false;
    private Boolean articleOfAssociation = false;
    private Boolean ownerPicture = false;
}
