package com.aes.erp.purchase_order.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class PoReceiveRequestDto {
    
    private List<PoRequestDto> purchaseOrders;
}
