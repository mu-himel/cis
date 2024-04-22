package com.aes.erp.inventory.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class BulkDeleteDto {
    private List<Long> id;
    
}
