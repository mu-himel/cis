package com.aes.erp.indent.repository;

import com.aes.erp.indent.entity.Indent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IndentRepository extends JpaRepository<Indent, Long>, IndentQuery {

    @Query(value = getReadyIndentWithSearch,
            countQuery = countReadyIndentWithSearch,
            nativeQuery = true
    )
    Page<IndentInfo> getAllIndents(
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            @Param("priority") String priority,
            Pageable pageable
    );

    @Query(value = getIndentByIdWithSearch,
            nativeQuery = true
    )
    List<IndentViewInfo> getIndentById(
            @Param("indentId") Long indentId
    );
    @Query(value = getIndentByIdsWithSearch,
            nativeQuery = true
    )
    List<IndentViewInfo> getIndentByIds(
            @Param("indentIds") List<Long> indentIds
    );

    @Modifying
    @Query(value = "UPDATE Indent i SET i.status = 'OPEN' WHERE i.id in (:indentIds)")
    int moveIndentByIds(
            @Param("indentIds") List<Long> indentIds
    );

    @Modifying
    @Query(value = """
                    update IndentDetail 
                    set orderQty = :orderQty
                    where id = :indentDetailId
                    and indent.id =:indentId
                """ )
    int updateOrderDetailsOrderQty(
            @Param("orderQty") Long orderQty,
            @Param("indentDetailId") Long indentDetailId,
            @Param("indentId") Long indentId
    );

    @Query("select max(i.id) from Indent i")
    Optional<Long> findMaxIndentById();


    interface IndentInfo {
        Long getId();

        Long getIndentNo();

        Long getCategoryId();

        String getCategoryName();

//        Long getSubCategoryId();
//
//        String getSubCategoryName();


        Long getOrderQty();

        String getPriority();
        String getStatus();

    }

    interface IndentViewInfo {
        Long getId();

        Long getIndentNo();

        Long getCategoryId();

        String getCategoryName();

        Long getSubCategoryId();

        String getSubCategoryName();

        Long getItemId();

        String getItemName();

        String getItemDescription();

        Long getOrderQty();

        String getPriority();
        Long getCurrentStock();

        Long getSafetytStock();

        Long getTransitQty();

        Long getDaysRemain();
        String getDeliveryLocation();
        String getPrQty();

    }
}
