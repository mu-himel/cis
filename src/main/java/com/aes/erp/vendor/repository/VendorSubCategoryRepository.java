package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.VendorSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

@Repository
public interface VendorSubCategoryRepository extends JpaRepository<VendorSubCategory, Long> {

    @Query("SELECT vs FROM VendorSubCategory vs WHERE vs.vendor.id = :vendorId AND vs.subcategory.id = :subCategoryId")
    VendorSubCategory findByVendorIdAndSubCategoryId(@Param("vendorId") Long vendorId, @Param("subCategoryId") Long subCategoryId);

}
