package com.aes.erp.vendor.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InitiatorDetailsDto {
    private String employeeId;
    private String employeeName;
    private String employeeDesignation;
    private String employeeDepartment;
    private String reportingManager;
    private String employeeWarehouse;
    private String warehouseLocation;
}
