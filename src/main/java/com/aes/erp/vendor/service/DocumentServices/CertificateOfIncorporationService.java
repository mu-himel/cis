package com.aes.erp.vendor.service.DocumentServices;


import com.aes.erp.vendor.document_response_dto.CertificateOfIncorporationDto;
import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;

public interface CertificateOfIncorporationService {
    CertificateOfIncorporation create(CertificateOfIncorporation certificateOfIncorporation);
    void update(Long documentHolderId, CertificateOfIncorporationDto dto);
}
