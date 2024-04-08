package com.aes.erp.inventory.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class BulkDeleteDto {
    List<Long> id;
    
}
