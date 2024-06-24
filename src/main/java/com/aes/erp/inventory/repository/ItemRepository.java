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
            @Param("storeTypeId")Long storeTypeId, Pageable pageable
    );


    List<ItemInfo> findAllByActiveAndNameLikeIgnoreCase(Boolean active, String name);

    List<ItemInfo> findAllByActiveAndCodeLikeIgnoreCase(Boolean active, String code);

    boolean existsByCodeAndActive(String code,Boolean active);

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

    @Query(value = """
            SELECT * FROM (SELECT i.id, i.brand_id ,i.active,
                    GROUP_CONCAT(DISTINCT ia.attribute_type,' ',ia.attribute_value , ' ',
                    ia.attribute_unit order by ia.id asc separator ' - ') itemAttributes
            FROM item_attributes ia
            LEFT JOIN items i on i.id=ia.item_id
            GROUP BY i.id) p
            WHERE p.active = 1 AND p.brand_id=:brandId AND itemAttributes = :attribute
            """,nativeQuery = true)
        List<ItemInfoByAttribute> findByAttributes(Long brandId, String attribute);

        @Query(value = """
            SELECT * FROM (SELECT i.id, i.brand_id ,i.active,
                    GROUP_CONCAT(DISTINCT ia.attribute_type,' ',ia.attribute_value , ' ',
                    ia.attribute_unit order by ia.id asc separator ' - ') itemAttributes
            FROM item_attributes ia
            LEFT JOIN items i on i.id=ia.item_id
            GROUP BY i.id) p
            WHERE p.brand_id=:brandId AND itemAttributes = :attribute
            """,nativeQuery = true)
        List<ItemInfoByAttribute> findByAttributesNotActive(Long brandId, String attribute);

        interface ItemInfoByAttribute{
                Long getBrandId();
                Long getId();
                String getItemAttributes();
        }

        @Query("SELECT i FROM Item i LEFT JOIN FETCH i.itemCategory ic " +
        "LEFT JOIN FETCH i.itemParentCategory ipc " +
        "LEFT JOIN FETCH ic.parentCategory pc WHERE ic.code=:subCatcode AND i.active=:b")
        List<Item> findAllByItemCategoryIdAndActive(@Param("subCatcode") String subCatcode, @Param("b") Boolean active);


        Optional<Item> findByCode(String code);

        @Query("SELECT i FROM Item i LEFT JOIN FETCH i.itemCategory ic " +
        "LEFT JOIN FETCH i.itemParentCategory ipc " +
        "LEFT JOIN FETCH ic.parentCategory pc WHERE i.active=false AND i.code IS NULL")
        List<Item> findAllInactiveItems();

}
