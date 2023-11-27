package com.aes.erp.productrequirment.repository;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.productrequirment.entity.ProductRequirement;
import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Repository
public interface ProductRequirementRepository extends JpaRepository<ProductRequirement, Long>, ProductRequirementQuery {

    @Query(value = getProductRequirementWithSearch,
            countQuery = countProductRequirementWithSearch,
            nativeQuery = true
    )
    Page<ProductRequirementInfo> findAllProductRequirements(
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );

    @Query(value = getProductRequirementViewWithSearch,
            nativeQuery = true
    )
    List<ProductRequirementViewInfo> getAllProductRequirementView(
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId
    );

    @Transactional
    @Modifying
    @Query("UPDATE ProductRequirement p SET p.status = :toStatus " +
            "WHERE p.status = :fromStatus AND p.category.id = :categoryId AND p.subCategory.id = :subCategoryId")
    int updateStatusByCategoryAndSubCategory(@Param("toStatus")ProductRequirmentStatus toStatus,
                                             @Param("fromStatus")ProductRequirmentStatus fromStatus,
                                             @Param("categoryId") Long categoryId,
                                             @Param("subCategoryId") Long subCategoryId);


    interface ProductRequirementInfo {
        Long getCategoryId();

        String getCategoryName();

        Long getSubCategoryId();

        String getSubCategoryName();

        Long getDaysRemain();

        Long getItemsQty();

    }

    interface ProductRequirementViewInfo {
        Long getCategoryId();

        String getCategoryName();

        Long getSubCategoryId();

        String getSubCategoryName();

        Long getItemId();

        String getItemName();

        String getItemDescription();

        Long getPrQty();

        Long getItemsQty();

        Long getCurrentStock();

        Long getSafetytStock();

        Long getTransitQty();

        Long getDaysRemain();

        String getPriority();

    }
}
