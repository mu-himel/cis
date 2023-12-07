package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.BINDocument;
import com.aes.erp.vendor.entity.DocmentEntities.TradeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TRADERepository extends JpaRepository<TradeDocument, Long> {
    @Query(value = "SELECT * FROM trade_license tld " +
            "WHERE tld.document_holder_id = :document_holder_id", nativeQuery = true)
    TradeDocument getTradeLicenseDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolder);
}
