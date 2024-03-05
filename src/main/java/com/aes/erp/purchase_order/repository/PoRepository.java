package com.aes.erp.purchase_order.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aes.erp.purchase_order.entity.PurchaseOrder;

@Repository
public interface PoRepository extends JpaRepository<PurchaseOrder,Long>,PurchaseQuery{

    @Query(value=pendingPos, countQuery = countPendingPos, nativeQuery=true)
    Page<PendingPOItem> findAllPendingPOs(@Param("vendorId") Long vendorId, Pageable pageable);
    
    interface PendingPOItem{
        Long getId();
        String getPoNo();
        String getTenderNo();
        String getProductType();
        Long getPoDate();
        Long getDeliveryDate();
        Long getItemQty();
        String getPoStatus();
    }
}
