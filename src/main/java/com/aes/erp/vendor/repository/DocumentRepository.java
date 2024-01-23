package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.BankSolvencyDocument;
import com.aes.erp.vendor.entity.DocmentEntities.Document;
import com.aes.erp.vendor.entity.DocmentEntities.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    @Query(value = "SELECT * FROM documents d " +
            "WHERE d.document_holder_id = :document_holder_id" +
            " AND d.document_type = :documentType" +
            " ORDER BY d.id DESC LIMIT 1", nativeQuery = true)
    Document getDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder,
                                           @Param("documentType") int documentType);

}
