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

    @Query(value="""
        SELECT ti.id as id, ti.item_attribute_name as itemAttributeName,
        b.name as brandName, ic.name as categoryName, ic.code as categoryCode, ipc.name as parentCategoryName, ipc.code as parentCategoryCode,
        ti.code as productCode
        FROM temp_items ti 
        LEFT JOIN item_categories ic ON ic.id = ti.item_category_id
        LEFT JOIN item_categories ipc ON ipc.id = ti.item_parent_category_id
        LEFT JOIN brands b ON b.id = ti.brand_id
        WHERE ti.active=false AND ipc.id=:parentCategoryId AND ic.id=:categoryId""",nativeQuery = true)
        List<TempItemResponseInfo> findAllInactiveItems(Long parentCategoryId, Long categoryId);

    @Query(value = """
            select substring_index(code,CONCAT(:prefix,'-'),-1) FROM (
                select max(code) code FROM (SELECT id, code, true as active from items i\s
                                                        WHERE code LIKE CONCAT(:prefix,'%')
                                                                     UNION
                                                                     SELECT id, code, false as active from temp_items pir WHERE code LIKE CONCAT(:prefix,'%')) p
                                 WHERE p.code LIKE CONCAT(:prefix,'%') ORDER BY code asc ) p
            """, nativeQuery = true)
    Long findNextCodeByCount(String prefix);

    @Query(value = "SELECT ti FROM TempItem ti WHERE ti.code = :code")
    Optional<TempItem> findByCode(String code);

    /**
         * TempItemResponseInfo
         */
        public interface TempItemResponseInfo {
                Long getId();
                String getItemAttributeName();
                String getBrandName();
                String getCategoryName();
                String getCategoryCode();
                String getParentCategoryName();
                String getParentCategoryCode();
                String getProductCode();
                
        }
}
