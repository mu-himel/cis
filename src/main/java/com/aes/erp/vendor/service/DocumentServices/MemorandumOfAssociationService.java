package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.CertificateOfIncorporationDto;
import com.aes.erp.vendor.document_response_dto.MemorandumOfAssociationDto;
import com.aes.erp.vendor.entity.DocmentEntities.MemorandumOfAssociation;

public interface MemorandumOfAssociationService {
    MemorandumOfAssociation create(MemorandumOfAssociation memorandumOfAssociation);
    void update(Long documentHolderId, MemorandumOfAssociationDto dto);
}
