package com.aes.erp.vendor.repository;
import com.aes.erp.vendor.entity.DocmentEntities.BankSolvencyDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BankSolvencyRepository extends JpaRepository<BankSolvencyDocument, Long> {
    @Query(value = "SELECT * FROM bank_solvency bd " +
            "WHERE bd.document_holder_id = :document_holder_id", nativeQuery = true)

    BankSolvencyDocument getBankSolvencyDocumentByDocumentHolderId(@Param("document_holder_id") Long documentHolderId);
}
