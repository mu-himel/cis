package com.aes.erp.vendor.dto;

import com.aes.erp.vendor.document_response_dto.*;
import lombok.Data;

@Data
public class ExtractedInformationDto {
    private BinResponseDto bin;
    private TinResponseDto tin;
    private TradeLicenseDto trade;
    private NidResponseDto nid;
    private BankSolvencyDto solvency;
    private ArticleOfAssociationDto articleOfAssociationDto;
    private MemorandumOfAssociationDto memorandumOfAssociationDto;
    private CertificateOfIncorporationDto certificateOfIncorporationDto;
}
