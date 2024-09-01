package com.aes.erp.inventory.repository;

public interface CategoryQuery {
    String getCategoriesWithSearch="SELECT cat.id, cat.code, cat.name FROM (" +
            "SELECT ic.id, ic.code, ic.name, " +
            "(sum(amount) + COALESCE((" +
            "        SELECT sum(amount) FROM item_categories childCat " +
            "        LEFT JOIN category_budgets cb2 ON childCat.id = cb2.category_id " +
            "    WHERE childCat.parent_category_id = ic.id " +
            "    AND cb2.current_year = :year " +
            "    ),0) ) as currentYearBudget, " +
            "    ( SELECT count(i.id) FROM items i " +
            "    WHERE i.item_category_id in ( " +
            "           SELECT id FROM item_categories ic3" +
            "           WHERE ic3.parent_category_id = ic.id )" +
            ") as productCount" +
            "     FROM item_categories ic " +
            "     LEFT JOIN category_budgets cb ON ic.id = cb.category_id " +
            "     WHERE ic.active=1 AND ic.parent_category_id IS NULL AND cb.current_year = :year " +
            "     GROUP BY ic.id) cat " +
            "WHERE (:name IS NULL OR cat.name LIKE concat(:name,'%')) " +
            " AND (:code IS NULL OR cat.code LIKE concat(:code,'%')) " +
            " AND (:currentYearBudget IS NULL OR cat.currentYearBudget LIKE concat(:currentYearBudget,'%')) " +
            " AND (:productCount IS NULL OR cat.productCount=:productCount)";

    String countCategoriesWithSearch="SELECT count(cat.id) FROM (" +
            "SELECT ic.id, ic.code, ic.name, " +
            "(sum(amount) + COALESCE((" +
            "        SELECT sum(amount) FROM item_categories childCat " +
            "        LEFT JOIN category_budgets cb2 ON childCat.id = cb2.category_id " +
            "    WHERE childCat.parent_category_id = ic.id " +
            "    AND cb2.current_year = :year " +
            "    ),0) ) as currentYearBudget, " +
            "    ( SELECT count(i.id) FROM items i " +
            "    WHERE i.item_category_id in ( " +
            "           SELECT id FROM item_categories ic3" +
            "           WHERE ic3.parent_category_id = ic.id )" +
            ") as productCount" +
            "     FROM item_categories ic " +
            "     LEFT JOIN category_budgets cb ON ic.id = cb.category_id " +
            "     WHERE ic.active=1 AND ic.parent_category_id IS NULL AND cb.current_year = :year " +
            "     GROUP BY ic.id) cat " +
            "WHERE (:name IS NULL OR cat.name LIKE concat(:name,'%')) " +
            " AND (:code IS NULL OR cat.code LIKE concat(:code,'%')) " +
            " AND (:currentYearBudget IS NULL OR cat.currentYearBudget LIKE concat(:currentYearBudget,'%')) " +
            " AND (:productCount IS NULL OR cat.productCount=:productCount)";


    String getSubCategoriesWithSearch="SELECT cat.id, cat.code, cat.name, cat.currentYearBudget, cat.productCount, " +
            "cat.mainCategoryId, cat.mainCategoryName, cat.mainCategoryCode " +
            "FROM (" +
            "SELECT ic.id, ic.code, ic.name, ipc.id as mainCategoryId, " +
            "ipc.name as mainCategoryName, ipc.code as mainCategoryCode," +
            "(sum(amount) + COALESCE((" +
            "        SELECT sum(amount) FROM item_categories childCat " +
            "        LEFT JOIN category_budgets cb2 ON childCat.id = cb2.category_id " +
            "    WHERE childCat.parent_category_id = ic.id " +
            "    AND cb2.current_year = :year " +
            "    ),0) ) as currentYearBudget, " +
            "    ( SELECT count(i.id) FROM items i " +
            "    WHERE i.item_category_id in ( " +
            "           SELECT id FROM item_categories ic3" +
            "           WHERE ic3.id = ic.id )" +
            ") as productCount" +
            "     FROM item_categories ic " +
            "     LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id " +
            "     LEFT JOIN category_budgets cb ON ic.id = cb.category_id " +
            "     WHERE ic.active=1 AND ic.parent_category_id IS NOT NULL AND cb.current_year = :year " +
            "     GROUP BY ic.id) cat " +
            "WHERE (:name IS NULL OR cat.name LIKE concat(:name,'%')) " +
            " AND (:code IS NULL OR cat.code LIKE concat(:code,'%')) " +
            " AND (:currentYearBudget IS NULL OR cat.currentYearBudget LIKE concat(:currentYearBudget,'%')) " +
            " AND (:categoryId IS NULL OR cat.mainCategoryId =:categoryId) " +
            " AND (:productCount IS NULL OR cat.productCount=:productCount)";

    String countSubCategoriesWithSearch="SELECT count(cat.id) " +
            "FROM (" +
            "SELECT ic.id, ic.code, ic.name, ipc.id as mainCategoryId, " +
            "ipc.name as mainCategoryName, ipc.code as mainCategoryCode," +
            "(sum(amount) + COALESCE((" +
            "        SELECT sum(amount) FROM item_categories childCat " +
            "        LEFT JOIN category_budgets cb2 ON childCat.id = cb2.category_id " +
            "    WHERE childCat.parent_category_id = ic.id " +
            "    AND cb2.current_year = :year " +
            "    ),0) ) as currentYearBudget, " +
            "    ( SELECT count(i.id) FROM items i " +
            "    WHERE i.item_category_id in ( " +
            "           SELECT id FROM item_categories ic3" +
            "           WHERE ic3.id = ic.id )" +
            ") as productCount" +
            "     FROM item_categories ic " +
            "     LEFT JOIN item_categories ipc ON ipc.id = ic.parent_category_id " +
            "     LEFT JOIN category_budgets cb ON ic.id = cb.category_id " +
            "     WHERE ic.active=1 AND ic.parent_category_id IS NOT NULL AND cb.current_year = :year " +
            "     GROUP BY ic.id) cat " +
            "WHERE (:name IS NULL OR cat.name LIKE concat(:name,'%')) " +
            " AND (:code IS NULL OR cat.code LIKE concat(:code,'%')) " +
            " AND (:currentYearBudget IS NULL OR cat.currentYearBudget LIKE concat(:currentYearBudget,'%')) " +
            " AND (:categoryId IS NULL OR cat.mainCategoryId =:categoryId) " +
            " AND (:productCount IS NULL OR cat.productCount=:productCount)";



    String findAllByItemCategoryWithSubCategoryCount = "SELECT c.id AS categoryId, c.name AS categoryName, " +
            "c.code AS categoryCode, COUNT(sub.id) AS subcategoryCount,st.id as storeTypeId, st.name AS storeTypeName " +
            "FROM ItemCategory c " +
            "LEFT JOIN ItemCategory sub ON c.id = sub.parentCategory.id  AND sub.active = true AND sub.categoryStatus = 'APPROVED' " +
            "LEFT JOIN StoreType st ON c.storeType.id = st.id " +
            "WHERE c.parentCategory IS NULL AND (:storeTypeId IS NULL OR st.id = :storeTypeId) " +
            " AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(:name) || '%' ) " +
            " AND (:code IS NULL OR LOWER(c.code) LIKE LOWER(:code) || '%' )" +
            "AND c.active = true AND c.categoryStatus IN ('APPROVED') " +
            "GROUP BY c.id ORDER BY c.name asc";

    String countQueryForFindAllByItemCategoryWithSubCategoryCount = "SELECT COUNT(DISTINCT c.id) AS categoryCount " +
            "FROM ItemCategory c " +
            "LEFT JOIN ItemCategory sub ON c.id = sub.parentCategory.id  AND sub.active = true " +
            "LEFT JOIN StoreType st ON c.storeType.id = st.id " +
            "WHERE c.parentCategory IS NULL AND (:storeTypeId IS NULL OR st.id = :storeTypeId) " +
            " AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(:name) || '%' ) " +
            " AND (:code IS NULL OR LOWER(c.code) LIKE LOWER(:code) || '%' )" +
            "AND c.active = true AND c.categoryStatus IN ('APPROVED')";

    String findAllBySubCategoryFilteredByParentCategory = """
        SELECT 
        c.id AS subCategoryId,
        c.name AS subCategoryName, c.code as subCategoryCode,
        par.id AS parentCategoryId, 
        par.code AS parentCategoryCode, 
        par.name AS parentCategoryName,
        (SELECT COUNT(i.id)  FROM items i WHERE i.item_category_id = c.id and i.active=1) as products, 
        (SELECT COUNT(pb.id) FROM pending_brands pb WHERE pb.sub_category_id = c.id) as pendingBrands, 
        (SELECT COUNT(pa.id) FROM pending_attributes pa WHERE pa.sub_category_id = c.id) as pendingAttributes
        FROM item_categories c
        LEFT JOIN item_categories par ON par.id = c.parent_category_id 
        WHERE c.parent_category_id IS NOT NULL AND (:parent_category IS NULL OR par.id = :parent_category) 
        AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(:name)||'%') 
        AND (:code IS NULL OR LOWER(c.code) LIKE LOWER(:code)||'%') 
        AND c.active = true 
        GROUP BY c.id
        """;

    String findAllBySubCategoryFilteredByStoreTypeAndParentCategory = """
            SELECT 
            c.id AS subCategoryId,
            c.name AS subCategoryName, c.code as subCategoryCode,
            par.id AS parentCategoryId, 
            par.code AS parentCategoryCode, 
            par.name AS parentCategoryName,
            st.id as storeTypeId, 
            st.name AS storeTypeName,
            (SELECT COUNT(i.id)  FROM items i WHERE i.item_category_id = c.id) as products, 
            (SELECT COUNT(pb.id) FROM pending_brands pb WHERE pb.sub_category_id = c.id) as pendingBrands, 
            (SELECT COUNT(pa.id) FROM pending_attributes pa WHERE pa.sub_category_id = c.id) as pendingAttributes
            FROM item_categories c
            LEFT JOIN item_categories par ON par.id = c.parent_category_id 
            LEFT JOIN store_types st ON c.store_type_id = st.id 
            WHERE c.parent_category_id IS NOT NULL AND 
            (:store_type_id IS NULL OR st.id = :store_type_id) 
            AND (:parent_category IS NULL OR par.id = :parent_category) 
            AND (:name IS NULL OR LOWER(c.name) LIKE LOWER(:name)||'%') 
            AND (:code IS NULL OR LOWER(c.code) LIKE LOWER(:code)||'%') 
            AND c.active = true 
            GROUP BY c.id""";
    String countQueryForSubCategoryFilteredByStoreTypeAndParentCategory = "SELECT COUNT(*) " +
            "FROM ("+findAllBySubCategoryFilteredByStoreTypeAndParentCategory+") c";


    String countQueryForSubCategoryFilteredByParentCategory="SELECT COUNT(*) FROM ("+
                findAllBySubCategoryFilteredByParentCategory+") c";

}
