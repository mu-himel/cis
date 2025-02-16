package com.aes.erp.inventory.dto.request.import_item;

import com.aes.erp.inventory.dto.request.CategoryAttributeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemImportReq {
    private String key;
    private Long csvIndex;
    private String categoryName;
    private String subCategoryName;
    private String brandName;
    private String unit;
    private List<CategoryAttributeDto> attributeDtoList;
}
