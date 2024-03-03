package com.aes.erp.purchase_order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.purchase_order.entity.PurchaseOrder;

@Repository
public interface PoRepository extends JpaRepository<PurchaseOrder,Long>{
    
}
