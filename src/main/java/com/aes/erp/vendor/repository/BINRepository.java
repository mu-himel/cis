package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.aes.erp.vendor.entity.DocmentEntities.TINDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BINRepository extends JpaRepository<BINDocument, Long> {
    @Query(value = "SELECT * FROM bin b " +
            "WHERE b.document_holder_id = :document_holder_id", nativeQuery = true)
    BINDocument getBinDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder);
}
