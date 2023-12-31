package com.aes.erp.inventory.repository;

public interface ItemQuery {

    String getItemsWithSearch = "SELECT i.id as id, i.name as name, i.code as code, " +
            "ic.id as subCategoryId, ic.name as subCategoryName, ic.code as subCategoryCode, " +
            "ipc.id as categoryId, ipc.name as categoryName, ipc.code as categoryCode, " +
            "SUM(s.stockQty) as qty," +
            " i.stockThresholdQty as stockThresholdQty," +
            " i.reorderPercentage as reorderPercentage " +
            "FROM Item i " +
            "LEFT JOIN i.stocks s " +
            "LEFT JOIN i.itemCategory ic " +
            "LEFT JOIN i.itemParentCategory ipc " +
            "WHERE i.active=1 AND (:name IS NULL OR i.name LIKE concat(:name,'%')) " +
            "   AND (:code IS NULL OR i.code LIKE concat(:code,'%')) " +
            "   AND (:subCategoryId IS NULL OR ic.id = :subCategoryId) " +
            "   AND (:categoryId IS NULL OR ipc.id = :categoryId) " +
            "   AND (:reorderPercentage IS NULL OR i.reorderPercentage = :reorderPercentage) " +
            "   AND (:stockThresholdQty IS NULL OR i.stockThresholdQty = :stockThresholdQty) " +
            "GROUP BY i";

    String countItemsWithSearch = """
            SELECT count(i) FROM Item i 
            LEFT JOIN i.stocks s
            LEFT JOIN i.itemCategory ic
            LEFT JOIN i.itemParentCategory ipc
            WHERE i.active=1 AND (:name IS NULL OR i.name LIKE concat(:name,'%'))
               AND (:code IS NULL OR i.code LIKE concat(:code,'%'))
               AND (:subCategoryId IS NULL OR ic.id = :subCategoryId)
               AND (:categoryId IS NULL OR ipc.id = :categoryId)
               AND (:reorderPercentage IS NULL OR i.reorderPercentage = :reorderPercentage)
               AND (:stockThresholdQty IS NULL OR i.stockThresholdQty = :stockThresholdQty)
             GROUP BY i
             """;
}
