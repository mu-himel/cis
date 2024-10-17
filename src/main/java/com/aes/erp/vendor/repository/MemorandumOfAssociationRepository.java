package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.CertificateOfIncorporation;
import com.aes.erp.vendor.entity.DocmentEntities.MemorandumOfAssociation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MemorandumOfAssociationRepository extends JpaRepository<MemorandumOfAssociation,Long> {
    @Query(value = "SELECT * FROM memorandum_of_association c " +
            "WHERE c.document_holder_id = :document_holder_id", nativeQuery = true)
    MemorandumOfAssociation getMOADocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder);
}
