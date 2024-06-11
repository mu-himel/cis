package com.aes.erp.inventory.service;

import java.util.List;

import com.aes.erp.inventory.dto.request.bulk_gen.SearchInactiveProduct;
import com.aes.erp.inventory.dto.response.TempItemResponse;

public interface TempItemService {
    List<TempItemResponse> searchInactiveItems(SearchInactiveProduct searchInactiveProduct);
}
