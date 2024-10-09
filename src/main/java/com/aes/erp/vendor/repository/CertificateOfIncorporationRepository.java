package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CertificateOfIncorporationRepository extends JpaRepository<CertificateOfIncorporation,Long> {
    @Query(value = "SELECT * FROM certificate_of_incorporation c " +
            "WHERE c.document_holder_id = :document_holder_id", nativeQuery = true)
    CertificateOfIncorporation getCOIDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder);
}
