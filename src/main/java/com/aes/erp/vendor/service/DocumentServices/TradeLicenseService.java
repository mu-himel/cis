package com.aes.erp.vendor.service.DocumentServices;

import com.aes.erp.vendor.document_response_dto.TradeLicenseDto;
import com.aes.erp.vendor.entity.DocmentEntities.TradeDocument;

public interface TradeLicenseService {
    TradeDocument create(TradeDocument tradeDocument);
    void update(Long documentHolderId, TradeLicenseDto dto);
}
