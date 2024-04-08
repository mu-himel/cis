package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
    Optional<Brand> findByName(String name);

    @Query(value="""
            SELECT sb.id as id, TRIM(b.name) as name from brands b
            LEFT JOIN subcategory_brands sb ON sb.brand_id = b.id 
            WHERE sb.subcategory_id = :subCatId
            """,nativeQuery = true)
    List<SubCatBrandInfo> findAllBySubCategoryId(@Param("subCatId") Long subCatId);

    interface SubCatBrandInfo{
        String getId();
        String getName();
    }
}
