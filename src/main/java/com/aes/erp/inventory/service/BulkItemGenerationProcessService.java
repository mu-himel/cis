package com.aes.erp.inventory.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.aes.erp.inventory.dto.request.ActivateItemDto;
import com.aes.erp.inventory.entity.BulkGenBrand;
import com.aes.erp.inventory.entity.BulkItemGenConfig;
import com.aes.erp.inventory.entity.ItemCategory;


public interface BulkItemGenerationProcessService {

    

     void getPermuttedItems(BulkItemGenConfig config, List<ItemCategory> categories);

     public void activateItems(ActivateItemDto activateItemDto);

    Optional<Map<String,Object>> getLogByBulkProceessId(Long id);
}
