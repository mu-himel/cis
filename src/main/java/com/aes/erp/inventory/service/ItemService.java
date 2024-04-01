package com.aes.erp.inventory.service;

import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.entity.Item;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface ItemService {


    void createItem(ItemRequestDto itemRequestDto);

    void updateItem(Long id, ItemRequestDto itemRequestDto);

    void deleteItem(Long id);

    Optional<Item> getItemDetail(Long id);

    Page<?> getAllItems(Optional<Integer> page, Optional<Integer> size,
                                   Optional<String> name,
                                   Optional<String> code,
                                   Optional<Integer> reorderPercentage,
                                   Optional<Integer> stockThresholdQty,
                                   Optional<Long> categoryId,
                                   Optional<Long> subCategoryId

    );

    List<?> getAllItems(Optional<Long> categoryId,Optional<String> name, Optional<String> code);

    

    String getNextItemCode();

    List<?> getSubCategoryWiseItemListWithAttribute(Long subCategoryId);
}
