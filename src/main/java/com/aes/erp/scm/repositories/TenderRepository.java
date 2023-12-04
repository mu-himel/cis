package com.aes.erp.scm.repositories;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.scm.DtoCollection.TenderResponseDto;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.text.html.Option;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long> {


    @Query("SELECT t.id AS id, " +
            "t.tenderStatus AS tenderStatus, " +
            "t.tenderType AS tenderType, " +
            "t.itemQuantity AS itemQuantity, " +
            "t.tenderCreator AS tenderCreator, " +
            "t.itemCategory AS itemCategory, " +
            "t.creationDate AS creationDate " +
            "FROM Tender t " +
            "WHERE (:searchFilter IS NULL OR " +
            "LOWER(t.itemCategory.name) LIKE LOWER(CONCAT('%', :searchFilter, '%')) OR " +
            "LOWER(t.tenderCreator.name) LIKE LOWER(CONCAT('%', :searchFilter, '%'))) " +
            "AND (:tenderType IS NULL OR t.tenderType = :tenderType) " +
            "AND (:startDate IS NULL OR t.creationDate >= :startDate) " +
            "AND (:endDate IS NULL OR t.creationDate <= :endDate)")
    Page<TenderExt> getAllTenders(Pageable pageable,
                                  @Param("searchFilter") Optional<String> searchFilter,
                                  @Param("tenderType") Optional<TenderType> tenderType,
                                  @Param("startDate") Optional<Long> startDate,
                                  @Param("endDate") Optional<Long> endDate);

    interface TenderExt{
        Long getId();
        TenderStatus getTenderStatus();
        TenderType getTenderType();
        ItemCategory getItemCategory();
        Long getItemQuantity();
        Organization getTenderCreator();
        Long getCreationDate();
    }
}
