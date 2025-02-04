package com.aes.erp.inventory.repository;

public interface ItemQuery {

    String getItemsWithSearch = """
            SELECT i.id as id, i.name as name, i.code as code,
                              ic.id as subCategoryId, ic.name as subCategoryName, ic.code as subCategoryCode,
                              ipc.id as categoryId, ipc.name as categoryName, ipc.code as categoryCode
                              FROM items i
                              LEFT JOIN item_categories ic ON ic.id = i.item_category_id
                              LEFT JOIN item_categories ipc ON ipc.id = i.item_parent_category_id
                              LEFT JOIN store_types  st ON st.id = i.store_type_id
                              WHERE i.active=1 AND (:name IS NULL OR i.name LIKE concat(:name,'%'))\s
                                 AND (:code IS NULL OR i.code LIKE concat(:code,'%'))\s
                                 AND (:subCategoryId IS NULL OR ic.id = :subCategoryId)\s
                                 AND (:categoryId IS NULL OR ipc.id = :categoryId)\s
                                 AND (:storeTypeId IS NULL OR st.id = :storeTypeId)
                              GROUP BY i.id""";

    String countItemsWithSearch = "SELECT count(*) FROM (" + getItemsWithSearch + ") as total";
}
