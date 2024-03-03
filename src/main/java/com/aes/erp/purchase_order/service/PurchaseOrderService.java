package com.aes.erp.purchase_order.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.purchase_order.dto.request.PoRequestDto;

public interface PurchaseOrderService {

    void receivePO(ClaimResponseDto loggedInUser, PoRequestDto poDto);
    
}
