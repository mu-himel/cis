package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.bulk_gen.BulkGenConfigDto;

public interface BulkItemGenConfigService {
    Long saveConfig(BulkGenConfigDto bulkGenConfigDto);
}
