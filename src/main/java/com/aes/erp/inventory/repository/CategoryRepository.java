package com.aes.erp.inventory.repository;

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
            @Param("storeTypeId") Long storeTypeId, Pageable pageable);

    @Query(value = findAllByItemCategoryWithSubCategoryCount,
            countQuery = countQueryForFindAllByItemCategoryWithSubCategoryCount)
    List<ItemCategoryWithSubCategoryCountExt> findAllByItemCategoryWithSubCategoryCount(
            @Param("storeTypeId") Long storeTypeId);

    @Query(value = findAllBySubCategoryFilteredByStoreTypeAndParentCategory,
            countQuery = countQueryForSubCategoryFilteredByStoreTypeAndParentCategory)
    Page<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
            @Param("store_type_id") Long store_type_id,
            @Param("parent_category") Long parent_category,
            Pageable pageable);

    @Query(value = findAllBySubCategoryFilteredByStoreTypeAndParentCategory,
            countQuery = countQueryForSubCategoryFilteredByStoreTypeAndParentCategory)
    List<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
            @Param("store_type_id") Long store_type_id,
            @Param("parent_category") Long parent_category);

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


    Optional<Long> countAllByParentCategoryAndActive(ItemCategory itemCategory,Boolean active);

    boolean existsByCode(String code);

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
            " AND (:parentCategoryId IS NULL OR ic.parentCategory.id = :parentCategoryId)")
    List<ItemCategoryInfo> findAllSubCategories(Long parentCategoryId);

    @Query("select max(ic.id) from ItemCategory ic")
    Optional<ItemCategory> findMaxOrderById();

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
    }
    public interface ItemCategoryWithSubCategoryCountExt {
        Long getCategoryId();
        String getCategoryName();
        String getCategoryCode();
        Long getSubcategoryCount();
        String getStoreTypeName();
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
}
