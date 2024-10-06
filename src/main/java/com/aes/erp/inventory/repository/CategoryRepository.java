package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.dto.response.SubCategory;
import com.aes.erp.inventory.entity.ItemCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<ItemCategory, Long>, CategoryQuery {






@Query(value = findAllByItemCategoryWithSubCategoryCount,
            countQuery = countQueryForFindAllByItemCategoryWithSubCategoryCount)
    Page<ItemCategoryWithSubCategoryCountExt> findAllByItemCategoryWithSubCategoryCount(
            @Param("storeTypeId") Long storeTypeId,@Param("name") String name,@Param("code") String code, Pageable pageable);


    @Query(value = findAllByItemCategoryWithSubCategoryCount,
            countQuery = countQueryForFindAllByItemCategoryWithSubCategoryCount)
    List<ItemCategoryWithSubCategoryCountExt> findAllByItemCategoryWithSubCategoryCount(
            @Param("storeTypeId") Long storeTypeId, @Param("name") String name, @Param("code") String code);

    @Query(value = findAllBySubCategoryFilteredByStoreTypeAndParentCategory,
            countQuery = countQueryForSubCategoryFilteredByStoreTypeAndParentCategory,
            nativeQuery = true)
    Page<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
            @Param("store_type_id") Long store_type_id,
            @Param("parent_category") Long parent_category,
            @Param("name") String name, @Param("code") String code,
            Pageable pageable);

    @Query(value = findAllBySubCategoryFilteredByStoreTypeAndParentCategory, nativeQuery = true)
    List<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
            @Param("store_type_id") Long store_type_id,
            @Param("parent_category") Long parent_category,
            @Param("name") String name,
            @Param("code") String code
            );

        @Query(value = findAllBySubCategoryFilteredByParentCategory,
            countQuery = countQueryForSubCategoryFilteredByParentCategory,
            nativeQuery = true)
        Page<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByParentCategory(
                @Param("parent_category") Long parent_category,
                @Param("name") String name, @Param("code") String code,
                Pageable pageable);

        @Query(value = findAllBySubCategoryFilteredByParentCategory, nativeQuery = true)
        List<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByParentCategory(
                @Param("parent_category") Long parent_category,
                @Param("name") String name,
                @Param("code") String code);

    Optional<ItemCategory> findByCode(String code);


    @Query("SELECT ic FROM ItemCategory ic LEFT JOIN FETCH ic.budgets b WHERE ic.id=:id and b.category.id=:id and b.currentYear=:year")
    Optional<ItemCategory> findById(@Param("id") Long id, @Param("year") Integer Year);


    @Query(value = getSubCategoriesWithSearch,countQuery = countSubCategoriesWithSearch,nativeQuery = true)
    Page<SubCategoryInfoExt> findAllSubCategories(
            @Param("name") String name,
            @Param("code") String code,
            @Param("currentYearBudget") BigDecimal currentYearBudget,
            @Param("productCount") Long productCurrent,
            @Param("categoryId") Long categoryId,
            @Param("year") Integer year,
            Pageable pageable);

    @Query(value = "select ic.id,ic.name,pc.name as mainCategoryName,ic.code, sum(amount) currentYearBudget," +
            "(select count(i.id) from items i where i.item_category_id in (ic.id)) as productCount " +
            "FROM item_categories ic\n" +
            " LEFT JOIN item_categories as pc on pc.id = ic.parent_category_id" +
            " LEFT JOIN category_budgets cb on ic.id = cb.category_id \n" +
            "WHERE ic.parent_category_id =:parentCategoryId AND cb.current_year=:year " +
            "GROUP BY ic.id",nativeQuery = true)
    Page<SubCategoryInfoExt> findAllSubCategories(@Param("parentCategoryId") Long id,
                                                   @Param("year") Integer year, Pageable pageable);

    @Query(value = "SELECT i.id FROM items i WHERE i.item_category_id = :catId AND i.active = true",nativeQuery = true)
    List<Long> findAllExistingItemsForSubcategory(@Param("catId") Long id);


    Optional<Long> countAllByParentCategoryAndActive(ItemCategory itemCategory,Boolean active);

    Boolean existsByCode(String code);
    Boolean existsByNameAndActive(String name, Boolean active);

    @Query(value = "SELECT ic.id FROM item_categories ic WHERE ic.parent_category_id IS null AND ic.active = true AND ic.name = :catName" ,nativeQuery = true)
    List<Long> findDuplicateCategoryId(String catName);

    @Query(value = "SELECT ic.id FROM item_categories ic WHERE ic.parent_category_id IS NOT null AND ic.active = true AND ic.name = :subcatName" ,nativeQuery = true)
    List<Long> findDuplicateSubCategoryId(String subcatName);


    @Query(value = "SELECT ic.id as id, ic.name as name, ic.code as code FROM ItemCategory ic " +
            "WHERE ic.active = 1 AND ic.parentCategory IS NULL " +
            " AND (:name IS NULL OR ic.name LIKE concat(:name,'%')) " +
            " AND (:code IS NULL OR ic.code LIKE concat(:code,'%'))")
    List<ItemCategoryInfo> findAllMainCategories(String name, String code);

    @Query(value = "SELECT ic.id as id, ic.name as name, ic.code as code FROM ItemCategory ic " +
            "WHERE ic.active = 1 " +
            " AND (:parentCategoryId IS NULL OR ic.parentCategory.id = :parentCategoryId)" +
            " AND (:name IS NULL OR ic.name LIKE concat(:name,'%')) " +
            " AND (:code IS NULL OR ic.code LIKE concat(:code,'%'))")
    List<ItemCategoryInfo> findAllSubCategories(Long parentCategoryId,
                                                String name, String code);

    @Query(value = "SELECT ic.id as id, ic.name as name, ic.code as code FROM ItemCategory ic " +
            "WHERE ic.active = 1 " +
            " AND (:parentCategoryId IS NULL OR ic.parentCategory.id = :parentCategoryId)" +
            " AND (ic.name LIKE %:categoryName%)"
    )
    List<ItemCategoryInfo> findAllSubCategories(@Param("parentCategoryId") Long parentCategoryId, @Param("categoryName") String categoryName);

    @Query(value = """
            SELECT COALESCE(COUNT(*),0)+1 as autoCode  FROM item_categories sic
                WHERE 
                (:categoryId IS NULL OR sic.parent_category_id = :categoryId) 
                AND 
                SUBSTR(sic.name,1,1) = :key ORDER BY sic.name ASC
            """,nativeQuery = true)
    String findMaxOrderById(String key,Long categoryId);


    @Query(value = """
            SELECT COALESCE(COUNT(*),0)+1 as autoCode  FROM item_categories sic
                WHERE 
                (:categoryId IS NULL OR sic.parent_category_id = :categoryId) 
                AND 
                SUBSTR(sic.name,1,1) = :key 
                AND (
                    (:categoryId IS NOT NULL AND sic.code LIKE CONCAT(:prefix,'%'))
                OR
                    (:categoryId IS NULL AND SUBSTR(sic.code,1,1)=:prefix)
                )
                ORDER BY sic.name ASC
            """,nativeQuery = true)
    String findMaxOrderById(String key,String prefix, Long categoryId);

    @Query(value = "SELECT * FROM item_categories ic " +
            "WHERE (:id IS NOT NULL AND ic.id = :id) " +
            "AND ic.copied_from IS NULL", nativeQuery = true)
    Optional<ItemCategory> findRootReferenceEntity(@Param("id") Long id);
    @Query(value = "SELECT * FROM item_categories ic " +
            "WHERE (:id IS NOT NULL AND ic.id = :id) " +
            "AND (:vendorId IS NOT NULL AND ic.vendor_id = :vendorId)", nativeQuery = true)
    Optional<ItemCategory> findSavedCategoryForVendor(@Param("vendorId") Long vendorId, @Param("id") Long id);

    @Query(value = """
        SELECT ic.id as id, ic.name as name, ic.code as code FROM item_categories ic
                LEFT JOIN vendor_sub_category vsc ON vsc.subcategory_id = ic.id
            WHERE ic.parent_category_id IS NOT NULL
            AND vsc.vendor_id = :vendorId
            GROUP BY ic.id
            """, nativeQuery = true)
    List<SubCategory> findSCategoryForVendor(@Param("vendorId") Long vendorId);

    
    @Query("SELECT c FROM ItemCategory c " +
            "WHERE c.parentCategory IS NULL " +
            " AND LOWER(c.name) = :name AND c.categoryStatus = 'APPROVED' AND c.active=true")
    List<ItemCategory> findCategoryByNameIgnoreCase(@Param("name") String name);

    @Query("SELECT c FROM ItemCategory c " +
            "WHERE c.parentCategory IS NOT NULL " +
            " AND LOWER(c.name) = :name AND c.categoryStatus = 'APPROVED' AND c.active=true")
    List<ItemCategory> findCategoryBySubCatNameIgnoreCase(@Param("name") String name);

    @Query(value = getPendingCategories, countQuery =countPendingCategories, nativeQuery = true)
    Page<PendingItemCategoryListInfo> findAllPendingItemCategories(String name, String code, Pageable pageable);

    @Query(value = getPendingSubCategories, countQuery = countPendingSubCategories, nativeQuery = true)
    Page<PendingItemCategoryListInfo> findAllPendingSubCategories(Long parentId,String name, String code, Pageable pageable);


    @Query(value = """
        SELECT COUNT(*) as total FROM Item i 
        LEFT JOIN i.itemParentCategory ipc
        LEFT JOIN i.itemCategory ic 
            WHERE ipc.id=:id AND (:subCatId IS NULL OR ic.id=:subCatId)
            """)
    Integer findProductCountByCategoryId(Long id,Long subCatId);
    @Query(value = "SELECT COUNT(*) as total FROM ItemCategory ic " +
            "LEFT JOIN ic.parentCategory pc WHERE pc.id=:id")
    Integer findSubCategoryCountByCategoryId(Long id);

    @Query(value = """
        SELECT ic FROM ItemCategory ic WHERE ic.parentCategory IS NULL AND ic.active=true 
        AND (:name IS NULL OR ic.name LIKE concat(:name,'%'))
        AND (:code IS NULL OR ic.code LIKE concat(:code,'%'))""")
    List<ItemCategory> findAllItemCategory(
             @Param("name") String name, @Param("code") String code);

    @Query(value = """
        SELECT ic FROM ItemCategory ic 
        LEFT JOIN ic.parentCategory pc
        WHERE pc IS NOT NULL AND ic.active=true 
        AND (:categoryId IS NULL OR pc.id=:categoryId)
        AND (:name IS NULL OR ic.name LIKE concat(:name,'%'))
        AND (:code IS NULL OR ic.code LIKE concat(:code,'%'))""")
    List<ItemCategory> findAllSubCategory(
            @Param("categoryId") Long categoryId,
            @Param("name") String name, @Param("code") String code);

    Optional<ItemCategory> findByName(String name);

    Optional<ItemCategory> findByNameAndActive(String name, Boolean b);

    interface PendingItemCategoryListInfo{

        Long getId();
        String getCreatedAt();
        String getRequestedBy();
        String getOrganization();
        String getCategory();

        String getParentCategory();
        String getSubCategoryQty();
        String getProductQty();
    }
    interface ItemCategoryInfo {


        Long getId();
        String getName();
        String getCode();


    }
    public interface SubCategoryWithParentCategoryAndStoreTypeExt{
        Long getSubCategoryId();
        String getSubCategoryName();
        String getSubCategoryCode();
        Long getParentCategoryId();
        String getParentCategoryName();
        String getParentCategoryCode();
        String getStoreTypeName();
        Long getStoreTypeId();
        Long getPendingBrands();
        Long getPendingAttributes();
        Long getProducts();
    }
    public interface ItemCategoryWithSubCategoryCountExt {
        Long getCategoryId();
        String getCategoryName();
        String getCategoryCode();
        Long getSubcategoryCount();
        String getStoreTypeName();
        Long getStoreTypeId();
    }

    interface ItemCategoryInfoExt extends ItemCategoryInfo{


        BigDecimal getCurrentYearBudget();
        Long getProductCount();


    }

    interface SubCategoryInfoExt extends ItemCategoryInfoExt{
        Long getMainCategoryId();
        String getMainCategoryName();
        String getMainCategoryCode();
    }

    @Query(value="""
        SELECT ic from ItemCategory ic WHERE 
        ic.parentCategory.id IS NOT NULL AND ic.active=true
        AND (:categoryId IS NULL OR ic.parentCategory.id = :categoryId) AND 
        (:subCategoryId IS NULL OR ic.id=:subCategoryId)
    """)
    List<ItemCategory> findAllSubCategories(@Param("categoryId") Long categoryId,
    @Param("subCategoryId") Long subCategoryId);


    
}
