package com.aes.erp.purchase_order.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.aes.erp.inventory.entity.Organization;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aes.erp.purchase_order.entity.PoQcDetail;
import com.aes.erp.purchase_order.entity.PurchaseOrder;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.scm.Entities.TenderDeliveryDetail;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferDeliveryDetail;

@Repository
public interface PoRepository extends JpaRepository<PurchaseOrder, Long>, PurchaseQuery {

    @Query(value = "SELECT * FROM tender_delivery_details t WHERE t.tender_item_id = :tenderItemId", nativeQuery = true)
    List<TenderDeliveryDetail> findDeliveryDetailsFromTenderItem(@Param("tenderItemId") Long tenderItemId);

    @Query(value = pendingPos, countQuery = countPendingPos, nativeQuery = true)
    Page<PendingPOItem> findAllPendingPOs(@Param("vendorId") Long vendorId, Pageable pageable);

    @Query(value = closedPos, countQuery = countClosedPos, nativeQuery = true)
    Page<PendingPOItem> findAllClosedPOs(Long vendorId, Pageable pageable);

    Optional<PurchaseOrder> findByRemotePoId(Long id);

    interface PendingPOItem {
        Long getId();

        String getPoNo();

        String getTenderNo();

        String getProductType();

        Long getPoDate();

        Long getDeliveryDate();

        Long getItemQty();

        String getPoStatus();
        String getOrgName();
    }

    <T> Optional<T> findById(Long id, Class<T> t);

    interface PurchaseOrderInfo{
        Organization getOrg();

        Long getId();
        String getTenderNo();
        String getPoNo(); 
        Long getPoDate();
        String getGrnDeclineNote();
        Boolean getIsPoSent();
        Long getDeliveryDate();
        String getCategoryCode();
        String getPoStatus();
        String getInvoicePath();
        String getQcResult();
        Boolean getIsQcPass();
        String getQcDeclineNote();
        List<PoQcDetail> getQcDetails(); 
        List<PurchaseOrderDetailInfo> getOrderDetails();
        VendorInfo getVendor();
    }

    /**
     * PurchaseOrderDetailInfo
     */
    public interface PurchaseOrderDetailInfo {
    
        Long getId();

        BigDecimal getItemQty();

        OfferItemInfo getOfferItem();

        String getItemName();
        BigDecimal getDeliveryCharge();

        List<PurchaseOrderDeliveryDetailInfo> getPurchaseOrderDeliveryDetails();
    }

    public interface PurchaseOrderDeliveryDetailInfo{
        Long getWarehouseId();
        BigDecimal getItemQty();
        BigDecimal getDeliveryCharge();
        String getWarehouseAddress();
        String getWarehouseName();
    }

    /**
     * OfferItemInfo
     */
    public interface OfferItemInfo {
    
        Long getId();
        String getProductDescription();
        String getSpecification();
        Long getEstimatedDeliveryDays();
        Long getItemQuantity();

        Integer getWarrantyDuration();
        String getWarrantyUnit();
     
        PriceQuotation getPriceQuotation();
     
        OfferInfo getOffer();
    }

    /**
     * OfferInfo
     */
    public interface OfferInfo {
        Long getId();
        List<OfferDeliveryDetail> getWarehouses();
        Boolean getMushakIncluded();
        Boolean getVatIncluded();
        BigDecimal getVatPercent();
        BigDecimal getVatAmount();
        Boolean getAitIncluded();
        String getNote();
        BigDecimal getFinalOfferPrice();
        Long getCreditPaymentDays();
        Boolean getDeliveryChargeIncluded();
        BigDecimal getDeliveryChargeAmount();
        TenderInfo getTender();
    }

    /**
     * TenderInfo
     */
    public interface TenderInfo {
        Long getId();
        List<TenderItemInfo> getTenderItems();
    }

    /**
     * TenderItemInfo
     */
    public interface TenderItemInfo {
        Long getId();
        String getProductDescription();
        BigDecimal getOrderQuantity();
        List<TenderDeliveryDetailInfo> getDeliveryDetails();
        
    }

    /**
     * TenderDeliveryDetailInfo
     */
    public interface TenderDeliveryDetailInfo {
        Long getId();
        BigDecimal getDeliveryOrderQTY();
        String getWareHouseAddress();
        String getWareHouseName();
        Long getWarehouseId(); 
        
    }
    /**
     * VendorInfo
     *
     */
    public interface VendorInfo
    {
        Long getId();
        String getName();
        String getEmail();
        String getPhone();  
    }

    
}
