package com.aes.erp.inventory.dto.request;

import java.util.List;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.inventory.entity.PendingItemAttribute;
import com.aes.erp.inventory.entity.PendingItemRequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PendingItemRequestDto implements EntityConvertable<PendingItemRequest>{
    private String subCategoryCode;
    private Long scmItemId;
    private String brand;
    private String requestedBy;
    private String employeeId;
    private String reportingManager;
    private String designation;
    private String department;
    private Long warehouseId;
    private String warehouseName;
    private Long organizationId;
    private String warehouseLocation;
    private String extendedAttributes;
    private List<PendingItemAttribute> attributes;

    @Override
    public PendingItemRequest getEntity() {
        PendingItemRequest pir = new PendingItemRequest();
        pir.setReportingManager(this.reportingManager);
        pir.setAttributes(this.attributes);
        pir.setRequestedBy(this.requestedBy);
        pir.setEmployeeId(this.employeeId);
        pir.setDesignation(this.designation);
        pir.setDepartment(this.department);
        pir.setWarehouseId(this.warehouseId);
        pir.setWarehouseName(this.warehouseName);
        pir.setExtendedAttributes(this.extendedAttributes);
        pir.setWarehouseLocation(this.warehouseLocation);
        return pir;
    }

    

}
