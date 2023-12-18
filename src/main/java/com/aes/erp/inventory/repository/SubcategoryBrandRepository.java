package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.SubcategoryBrand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubcategoryBrandRepository extends JpaRepository<SubcategoryBrand, Long> {
    @Query(value = "SELECT * FROM subcategory_brands ", nativeQuery = true)
    Page<SubcategoryBrandExt> getAllSubcategoryBrands(Pageable pageable);

    interface SubcategoryBrandExt{
        Long getId();
        String getName();
    }
    @Query("SELECT sb FROM SubcategoryBrand sb " +
            "WHERE sb.name = :name AND sb.category.id = :subcategoryId")
    Optional<SubcategoryBrand> getBrandByIdAndSubCategory(@Param("name") String name, @Param("subcategoryId") Long subcategoryId);
}
