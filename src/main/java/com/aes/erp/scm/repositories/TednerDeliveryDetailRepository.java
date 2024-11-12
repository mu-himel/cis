package com.aes.erp.scm.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aes.erp.scm.Entities.TenderDeliveryDetail;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Repository
public interface TednerDeliveryDetailRepository extends JpaRepository<TenderDeliveryDetail,Long>{
    List<TenderDeliveryDetail> findByTenderItemId(Long tenderItemId);

    @Query(value = "SELECT DISTINCT * FROM tender_delivery_details tdd WHERE tdd.warehouse_id = :warehouseId LIMIT 1",nativeQuery = true)
    Optional<TenderDeliveryDetail> getTenderDeliveryDetailByWarehouseId(Long warehouseId);

    @Query(value="""
        SELECT tdd.warehouseId,tdd.deliveryOrderQty,tdd.tenderItemId,ic.scm_categoryId,
        (SELECT offer_id FROM tender_participators tp WHERE vendor_id=:vendorId AND tender_id = :tenderId AND status = 'AWARDED') as offerId
        FROM tender_delivery_details tdd 
        JOIN tender_items ti ON tdd.tender_item_id = ti.id 
        JOIN tenders t ON ti.tender_id = t.id 
        JOIN item_categories ic ON t.item_category_id = ic.id
        WHERE t.id = :tenderId""",nativeQuery = true)
    List<POItemDeliveryInfo> getItemWiseDeliveryDetailPO(@Param("tenderId") Long tenderId, @Param("vendorId") Long vendorId);

    interface POItemDeliveryInfo {
        Long getWarehouseId();
        Long getScmCategoryId();
        Long getOfferId();
        BigDecimal getDeliveryOrderQty();
        Long getTenderItemId();
    }
}
