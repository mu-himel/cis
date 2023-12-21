package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.GeneralDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralDetailsRepository extends JpaRepository<GeneralDetails, Long> {
    @Query(value = "SELECT * FROM general_details gd WHERE gd.document_holder_id = :documentHolderId", nativeQuery = true)
    Optional<GeneralDetails> findByDocumentHolderId(@Param("documentHolderId") Long documentHolderId);
}
