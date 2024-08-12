package com.aes.erp.inventory.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.inventory.dto.request.ActivateItemDto;
import com.aes.erp.inventory.dto.request.ImportItemScmIdUpdateDto;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.dto.request.MergePendingItemsDto;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface ItemService {


    void createItem(ClaimResponseDto loggedInUser,ItemRequestDto itemRequestDto);

    void updateItem(Long id, ItemRequestDto itemRequestDto);

    void deleteItem(Long id);

    Optional<Item> getItemDetail(Long id);

    Page<?> getAllItems(Optional<Integer> page, Optional<Integer> size,
                                   Optional<String> name,
                                   Optional<String> code,
                                   Optional<Integer> reorderPercentage,
                                   Optional<Integer> stockThresholdQty,
                                   Optional<Long> categoryId,
                                   Optional<Long> subCategoryId,
                                   Optional<Long> storeTypeId

    );

    List<?> getAllItems(Optional<Long> categoryId,Optional<String> name, Optional<String> code);

    String getNextItemCode();

    List<?> getSubCategoryWiseItemListWithAttribute(String subCatcode);

    CategoryService getCategoryService();

   

    List<?> getAllInactiveItems(Long parentCategoryId, Long categoryId);

    void activateItems(ActivateItemDto activateItemDto);

    void mergePendingItems(Long id, MergePendingItemsDto mergePendingItemsDto);

    void updateItemScmId(List<ImportItemScmIdUpdateDto> itemScmIdList);


}
