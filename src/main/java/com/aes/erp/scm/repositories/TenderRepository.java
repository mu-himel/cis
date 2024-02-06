package com.aes.erp.scm.repositories;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderStatus;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenderRepository extends JpaRepository<Tender, Long>, TenderQuery {
    Page<Tender> findAll(Specification<Tender> specification, Pageable pageable);

    @Query(value = tenderProjectionQuery, countQuery = tenderProjectionCountQuery)
    Page<TenderExt> findAllTenderProjection(@Param("searchFilter") String searchFilter,
                                            @Param("tenderType") Optional<TenderType> tenderType,
                                            @Param("startDate") Optional<Long> startDate,
                                            @Param("endDate") Optional<Long> endDate,
                                            Pageable pageable);

    interface TenderExt{
        Long getId();
        TenderStatus getTenderStatus();
        TenderType getTenderType();
        ItemCategory getItemCategory();
        Organization getTenderCreator();
        Long getCreationDate();
        Long getTenderItemCount();
    }
}
