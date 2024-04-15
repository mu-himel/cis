package com.aes.erp.inventory.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.PendingAttribute;

@Repository
public interface PendingAttributeRepository extends JpaRepository<PendingAttribute,Long>{

    List<PendingAttribute> findAllBySubCategoryId(Long subCatId);

    @Query(value="""
            SELECT pa FROM PendingAttribute pa WHERE pa.subCategory.id = :id AND
            LOWER(pa.attributeType) = LOWER(:attributeType) AND LOWER(pa.attributeValue) = LOWER(:attributeValue)
            """
    )
    Optional<PendingAttribute> findAllBySubCategoryIdAndAttributeTypeAndAttributeValue(
        @Param("id") Long id, @Param("attributeType") String attributeType,
        @Param("attributeValue") String attributeValue);

    
    
}
