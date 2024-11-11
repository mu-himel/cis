package com.aes.erp.purchase_order.repository;

import com.aes.erp.purchase_order.entity.PurchaseOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PoDeliveryDetailsRepository extends JpaRepository<PurchaseOrderDetail,Long> {
}
