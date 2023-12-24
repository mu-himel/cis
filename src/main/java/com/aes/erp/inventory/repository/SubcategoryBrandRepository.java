package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.SubCategoryBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubcategoryBrandRepository extends JpaRepository<SubCategoryBrand, Long> {
    @Query("SELECT sb FROM SubCategoryBrand sb " +
            "WHERE sb.brand.name = :name AND " +
            "(sb.subcategory.id = :subcategoryId OR :subcategoryId IS NULL)")
    Optional<SubCategoryBrand> getBrandByNameAndSubCategoryId(@Param("name") String name, @Param("subcategoryId") Long subcategoryId);
}
