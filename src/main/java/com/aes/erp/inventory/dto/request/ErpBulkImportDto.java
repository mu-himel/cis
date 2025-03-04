package com.aes.erp.inventory.dto.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ErpBulkImportDto extends BulkDeleteDto{
    private String userId;
    private Long warehouseId;
    private Long warehouseStoreId;
    private Long parentCategoryId;
    private Boolean isForCps;
    private Boolean isSync;
}
