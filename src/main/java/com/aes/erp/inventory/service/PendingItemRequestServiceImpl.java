package com.aes.erp.inventory.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aes.erp.inventory.dto.request.PendingItemRequestDto;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.aes.erp.inventory.repository.PendingItemRequestRepository;

@Service
public class PendingItemRequestServiceImpl implements PendingItemRequestService{

    @Autowired
    private PendingItemRequestRepository pendingItemRequestRepository;

    @Override
    public void createPendingItemRequest(PendingItemRequestDto pRequestDto) {
    
        PendingItemRequest pir = new PendingItemRequest();

        pendingItemRequestRepository.save(pir);
        
    }
    
}
