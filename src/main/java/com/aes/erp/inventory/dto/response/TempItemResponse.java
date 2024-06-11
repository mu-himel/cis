package com.aes.erp.inventory.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempItemResponse {
    private Long id;
    private String itemAttributeName;
    private String brandName;
    private String categoryName;
    private String parentCategoryName;
    private String categoryCode;
    private String parentCategoryCode;
    private String productCode;
}
