package com.aes.erp.purchase_order.service;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.purchase_order.dto.request.PoRequestDto;

public interface PurchaseOrderService {

    void receivePO(ClaimResponseDto loggedInUser, PoRequestDto poDto);
    
    Page<?> getPendingPOs(ClaimResponseDto loggedInUser, Optional<Integer>page, Optional<Integer>size);

    <T> Optional<T> getDetailById(Long id, Class<T> t);

    Page<?> getClosedPOs(ClaimResponseDto loggedInUser, Optional<Integer> page, Optional<Integer> size);
}
