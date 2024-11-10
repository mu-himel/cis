package com.aes.erp.purchase_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.purchase_order.entity.PurchaseOrderDetail;

import java.util.List;

@Repository
public interface PoDetailRepository extends JpaRepository<PurchaseOrderDetail, Long>{
    
    List<PurchaseOrderDetail> findByPurchaseOrderId(Long purchaseOrderId);
}
