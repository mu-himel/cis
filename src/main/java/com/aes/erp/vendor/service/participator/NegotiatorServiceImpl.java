package com.aes.erp.vendor.service.participator;

import com.aes.erp.vendor.entity.RFQ_Negotiation.Negotiator;
import com.aes.erp.vendor.repository.NegotiatorRepository;
import org.springframework.stereotype.Service;

@Service
public class NegotiatorServiceImpl implements NegotiatorService {
    private final NegotiatorRepository participatorRepository;

    public NegotiatorServiceImpl(NegotiatorRepository participatorRepository) {
        this.participatorRepository = participatorRepository;
    }

    @Override
    public Negotiator saveNegotiator(Negotiator participator) {
        return participatorRepository.save(participator);
    }
}
