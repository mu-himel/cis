package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.NIDDocument;
import com.aes.erp.vendor.entity.DocmentEntities.TINDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TINRepository extends JpaRepository<TINDocument, Long> {
    @Query(value = "SELECT * FROM tin td " +
            "WHERE td.document_holder_id = :document_holder_id", nativeQuery = true)
    TINDocument getTinDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder);
}
