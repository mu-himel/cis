package com.aes.erp.inventory.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.PendingItemRequest;

@Repository
public interface PendingItemRequestRepository extends JpaRepository<PendingItemRequest,Long>{

    String pendingItemReq="""
        SELECT 
        pir.id as id,
        request_no as requestNo,
        pir.created_at as createdAt,
        requested_by as requestedBy,
        c.name as categoryName,
        sc.name as subCategoryName,
        b.name as product
        FROM pending_item_requests pir
        LEFT JOIN item_categories c ON c.id = pir.category_id
        LEFT JOIN item_categories sc ON sc.id = pir.sub_category_id
        LEFT JOIN brands b ON b.id = pir.brand_id
    """;

    String countPendingItemReq="SELECT count(*) FROM ("+pendingItemReq+") c";

    @Query(value = pendingItemReq, countQuery = countPendingItemReq ,nativeQuery = true)
    Page<PendingItemReqInfo> findAllPendingItemRequests(Pageable pageable);

    interface PendingItemReqInfo{
        Long getId();
        String getRequestNo();
        LocalDateTime getCreatedAt();
        String getRequestedBy();
        String getCategoryName();
        String getSubCategoryName();
        String getProduct();
    }

    <T> Optional<T> findById(Long id, Class<T> classType);
    
}
