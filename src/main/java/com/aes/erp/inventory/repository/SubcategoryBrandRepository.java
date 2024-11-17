package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.SubCategoryBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryBrandRepository extends JpaRepository<SubCategoryBrand, Long> {
    @Query("SELECT sb FROM SubCategoryBrand sb " +
            "WHERE sb.brand.name = :name AND " +
            "(sb.subcategory.id = :subcategoryId OR :subcategoryId IS NULL)")
    Optional<SubCategoryBrand> getBrandByNameAndSubCategoryId(@Param("name") String name, @Param("subcategoryId") Long subcategoryId);

    List<SubCategoryBrand> findAllBySubcategoryId(Long id);

    Optional<SubCategoryBrand> findAllByBrandIdAndSubcategoryId(Long brandId,Long subcategoryId);

    void deleteBySubcategoryId(Long id);
}
