package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.BankSolvencyDocument;
import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NIDRepository extends JpaRepository<NIDDocument, Long> {
    @Query(value = "SELECT * FROM nid nd " +
            "WHERE nd.document_holder_id = :document_holder_id", nativeQuery = true)
    NIDDocument getNidDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder);
}
