package com.aes.erp.indent.repository;

import com.aes.erp.indent.entity.PrIndent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PrIndentRepository extends JpaRepository<PrIndent, Long>, PrIndentQuery {

    @Query(value = getReadyIndentWithSearch,
            countQuery = countReadyPrIndentWithSearch,
            nativeQuery = true
    )
    Page<PrIndentInfo> getAllPrIndents(
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            @Param("priority") String priority,
            Pageable pageable
    );

    @Query(value = getPrIndentByIdWithSearch,
            nativeQuery = true
    )
    List<PrIndentViewInfo> getPrIndentById(
            @Param("id") Long id
    );
    @Query(value = getPrIndentByIdsWithSearch,
            nativeQuery = true
    )
    List<PrIndentViewInfo> getPrIndentByIds(
            @Param("ids") List<Long> ids
    );


    @Modifying
    @Query(value = """
                update PrIndentDetail 
                set orderQty = :orderQty
                where id = :prIndentDetailId
                and prIndent.id =:prIndentId
            """)
    int updatePrIndentDetailsOrderQty(
            @Param("orderQty") Long orderQty,
            @Param("prIndentDetailId") Long prIndentDetailId,
            @Param("prIndentId") Long prIndentId
    );

    interface PrIndentInfo {
        Long getId();

        Long getIndentNo();

        Long getCategoryId();

        String getCategoryName();

        Long getSubCategoryId();

        String getSubCategoryName();

        Long getOrderQty();

        String getPriority();

    }

    interface PrIndentViewInfo {
        Long getId();
        Long getPrDetailId();
        Long getCategoryId();

        String getCategoryName();

        Long getSubCategoryId();

        String getSubCategoryName();

        Long getItemId();

        String getItemName();

        String getItemDescription();

        Long getOrderQty();
        String getPrQty();

        String getPriority();

        Long getCurrentStock();

        Long getSafetyStock();

        Long getTransitQty();

        Long getDaysRemain();
    }
}
