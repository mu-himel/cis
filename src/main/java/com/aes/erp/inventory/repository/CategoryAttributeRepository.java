package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.CategoryAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryAttributeRepository extends JpaRepository<CategoryAttribute,Long> {
    List<CategoryAttribute> findAllByCategoryId(Long categoryId);

    void deleteByIdAndCategoryId(Long attributeId, Long categoryId);

    Optional<CategoryAttribute> findByCategoryIdAndAttributeType(Long subCatId, String attributeType);
}
