package com.aes.erp.vendor.service.negotiation_history;

import com.aes.erp.exception.AesException;
import com.aes.erp.vendor.entity.RFQ_Negotiation.NegotiationHistory;
import com.aes.erp.vendor.repository.NegotiationHistoryRepository;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Optional;

@Service
public class NegotiationHistoryServiceImpl implements NegotiationHistoryService{
    private final NegotiationHistoryRepository negotiationHistoryRepository;

    public NegotiationHistoryServiceImpl(NegotiationHistoryRepository negotiationHistoryRepository) {
        this.negotiationHistoryRepository = negotiationHistoryRepository;
    }

    @Override
    public NegotiationHistory saveHistory(NegotiationHistory negotiationHistory) {
        return negotiationHistoryRepository.save(negotiationHistory);
    }

    @Override
    public NegotiationHistory getHistoryById(Long id) {
        Optional<NegotiationHistory> optionalNegotiationHistory = negotiationHistoryRepository.findById(id);
        if(optionalNegotiationHistory.isEmpty()) throw new AesException("Negotiation History couldn't be found");
        return optionalNegotiationHistory.get();
    }

    @Override
    public NegotiationHistory getByTender(Long tenderId, Long vendorId) {
        Optional<NegotiationHistory> optionalNegotiationHistory = negotiationHistoryRepository.getByTenderId(tenderId, vendorId);
        if(optionalNegotiationHistory.isEmpty()) throw new AesException("Negotiation History couldn't be found");
        return optionalNegotiationHistory.get();
    }
}
