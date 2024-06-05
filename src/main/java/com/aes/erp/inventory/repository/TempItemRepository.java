package com.aes.erp.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.TempItem;
import com.aes.erp.inventory.repository.ItemRepository.ItemInfoByAttribute;

@Repository
public interface TempItemRepository extends JpaRepository<TempItem,Long>{
    @Query(value = """
            SELECT * FROM (SELECT i.id, i.brand_id ,i.active,
                    GROUP_CONCAT(DISTINCT ia.attribute_type,' ',ia.attribute_value , ' ',
                    ia.attribute_unit order by ia.id asc separator ' - ') itemAttributes
            FROM temp_item_attributes ia
            LEFT JOIN temp_items i on i.id=ia.item_id
            GROUP BY i.id) p
            WHERE p.brand_id=:brandId AND itemAttributes = :attribute
            """,nativeQuery = true)
    List<ItemInfoByAttribute> findByAttributesNotActive(Long brandId, String attribute);

    @Query("SELECT max(ti.id) from TempItem ti")
    Optional<TempItem> findMaxOrderById();

    @Query("SELECT i FROM TempItem i LEFT JOIN FETCH i.itemCategory ic " +
        "LEFT JOIN FETCH i.itemParentCategory ipc " +
        "LEFT JOIN FETCH ic.parentCategory pc WHERE i.active=false")
        List<TempItem> findAllInactiveItems();
}
