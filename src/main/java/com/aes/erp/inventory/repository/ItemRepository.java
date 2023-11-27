package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item,Long>,ItemQuery {

    @Query("SELECT i FROM Item i LEFT JOIN FETCH i.itemCategory ic " +
            "LEFT JOIN FETCH i.itemParentCategory ipc " +
            "LEFT JOIN FETCH ic.parentCategory pc WHERE i.id=:id")
    Optional<Item> findById(@Param("id") Long id);


    @Query(value = getItemsWithSearch,
            countQuery = countItemsWithSearch)
    Page<PageItemList> findAllItems(
            @Param("name") String name,
            @Param("code") String code,
            @Param("reorderPercentage") Integer reorderPercentage,
            @Param("stockThresholdQty") Integer stockThresholdQty,
            @Param("categoryId") Long categoryId,
            @Param("subCategoryId") Long subCategoryId,
            Pageable pageable
    );


    List<ItemInfo> findAllByActiveAndNameLikeIgnoreCase(Boolean active, String name);

    List<ItemInfo> findAllByActiveAndCodeLikeIgnoreCase(Boolean active, String code);

    boolean existsByCode(String code);

    @Query("select max(i.id) from Item i")
    Optional<Item> findMaxOrderById();

    List<ItemInfo> findAllByActiveAndItemCategoryIdOrItemParentCategoryIdAndNameLikeIgnoreCaseOrCodeLikeIgnoreCase(
            Boolean active,
            Optional<Long> categoryId, Optional<Long> categoryId1, String name, String code);

    interface ItemInfo{
        Long getId();
        String getName();
        String getCode();
    }

    interface PageItemList extends ItemInfo{
        Long getCategoryId();
        Long getSubCategoryId();
        String getCategoryName();
        String getCategoryCode();
        String getSubCategoryName();
        String getSubCategoryCode();
        Integer getQty();
        Integer getStockThresholdQty();
        Integer getReorderPercentage();
    }


}
