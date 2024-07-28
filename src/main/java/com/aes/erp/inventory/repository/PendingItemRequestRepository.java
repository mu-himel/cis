package com.aes.erp.inventory.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Repository
public interface PendingItemRequestRepository extends JpaRepository<PendingItemRequest,Long>{

    String pendingItemReq="""
        SELECT 
        pir.id as id,
        request_no as requestNo,
        pir.created_at as createdAt,
        requested_by as requestedBy,
        c.name as categoryName,
        org.name as orgName,
        sc.name as subCategoryName,
        CASE WHEN b.name IS NOT NULL THEN
         concat(b.name,' - ', pir.item_attribute_name)
          ELSE pir.item_attribute_name
         end as product
        FROM pending_item_requests pir
        LEFT JOIN organizations org ON org.id = pir.organization_id
        LEFT JOIN item_categories c ON c.id = pir.category_id
        LEFT JOIN item_categories sc ON sc.id = pir.sub_category_id
        LEFT JOIN brands b ON b.id = pir.brand_id
        WHERE (:categoryId IS NULL OR pir.category_id=:categoryId)
        AND (:subCategoryId IS NULL OR pir.sub_category_id=:subCategoryId)
    """;

    String countPendingItemReq="SELECT count(*) FROM ("+pendingItemReq+") c";

    @Query(value = pendingItemReq, countQuery = countPendingItemReq ,nativeQuery = true)
    Page<PendingItemReqInfo> findAllPendingItemRequests(
            Long categoryId, Long subCategoryId, Pageable pageable);

    interface PendingItemReqInfo{
        Long getId();
        String getRequestNo();

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        LocalDateTime getCreatedAt();
        String getRequestedBy();
        String getCategoryName();
        String getSubCategoryName();
        String getOrgName();
        String getProduct();
    }

    <T> Optional<T> findById(Long id, Class<T> classType);

    @Query("SELECT max(pir.id) FROM PendingItemRequest pir")
    Optional<Long> findMaxOrderById();
    
}
