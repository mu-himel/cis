package com.aes.erp.vendor.service.negotiation_history;

import com.aes.erp.vendor.entity.RFQ_Negotiation.NegotiationHistory;

public interface NegotiationHistoryService {
    NegotiationHistory saveHistory(NegotiationHistory negotiationHistory);
    NegotiationHistory getHistoryById(Long id);
    NegotiationHistory getByTender(Long tenderId, Long vendorId);
}
