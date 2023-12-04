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
            nativeQuery = true)
    Page<ItemCategoryWithSubCategoryCountExt> findAllByItemCategoryWithSubCategoryCount(
            @Param("store_type_id") Long store_type_id, Pageable pageable);

    @Query(value = findAllByItemCategoryWithSubCategoryCount,
            nativeQuery = true)
    List<ItemCategoryWithSubCategoryCountExt> findAllByItemCategoryWithSubCategoryCount(
            @Param("store_type_id") Long store_type_id);

    @Query(value = "SELECT c.id AS sub_category_id, c.name AS sub_category_name, par.id AS parent_category, st.name AS store_type_name " +
            "FROM item_categories c " +
            "LEFT JOIN item_categories par ON par.id = c.parent_category_id " +
            "LEFT JOIN store_types st ON c.store_type_id = st.id " +
            "WHERE st.id = :store_type_id " +
            "AND par.id = :parent_category " +
            "GROUP BY c.id",
            nativeQuery = true)
    Page<SubCategoryWithParentCategoryAndStoreTypeExt> findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
            @Param("store_type_id") Long store_type_id,
            @Param("parent_category") Long parent_category,
            Pageable pageable);

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


    @Query("select max(ic.id) from ItemCategory ic")
    Optional<ItemCategory> findMaxOrderById();

    interface ItemCategoryInfo {


        Long getId();
        String getName();
        String getCode();


    }
    public interface SubCategoryWithParentCategoryAndStoreTypeExt{
        String getSub_category_id();
        String getSub_category_name();
        String getParent_category();
        String getStore_type_name();
    }
    public interface ItemCategoryWithSubCategoryCountExt {
        Long getCategoryId();
        String getCategoryName();
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
