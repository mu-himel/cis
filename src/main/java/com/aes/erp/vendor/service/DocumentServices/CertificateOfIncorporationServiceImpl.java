package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.BinResponseDto;
import com.aes.erp.vendor.document_response_dto.CertificateOfIncorporationDto;
import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import com.aes.erp.vendor.repository.CertificateOfIncorporationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CertificateOfIncorporationServiceImpl implements CertificateOfIncorporationService {
    @Autowired
    private CertificateOfIncorporationRepository certificateOfIncorporationRepository;
    @Autowired
    private DocumentService documentService;

    @Override
    public CertificateOfIncorporation create(CertificateOfIncorporation certificateOfIncorporation) {
        return certificateOfIncorporationRepository.save(certificateOfIncorporation);
    }

    @Override
    public void update(Long documentHolderId, CertificateOfIncorporationDto dto) {
        CertificateOfIncorporation entity = certificateOfIncorporationRepository.getCOIDocumentByDocumentHolderId(documentHolderId);
        entity = dto.dtoToEntityMapping(dto, entity);
        Document document = documentService.getDocumentByDocumentHolderIdAndType(documentHolderId, DocumentType.IRC);
        entity.setDocument(document);
        certificateOfIncorporationRepository.save(entity);
    }
}
