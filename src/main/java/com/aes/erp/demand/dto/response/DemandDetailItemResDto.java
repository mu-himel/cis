package com.aes.erp.demand.dto.response;

import com.aes.erp.demand.enums.DemandItemStatus;
import com.aes.erp.demand.enums.DemandPriority;
import com.aes.erp.demand.enums.DemandStatus;
import com.aes.erp.inventory.enums.ItemUnit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DemandDetailItemResDto {
    Long itemId;
    Long itemCategoryId;
    Long itemParentCategoryId;
    String name;
    String category;
    String parentCategory;
    String parentCategoryCode;
    String categoryCode;
    String code;
    Long demandDetailId;
    Long prQty;
    DemandStatus demandItemStatus;
    String specification;
    DemandPriority demandPriority;
    Integer approvedQuantity;
    Integer requestedQuantity;
    Integer currentStock;
    Integer stockThresholdQty;
    ItemUnit itemUnit;
    Integer totalStockInCurrentMonth;
    Integer totalConsumeInCurrentMonth;
    BigDecimal avgTotalConsumeInCurrentMonth;


}
