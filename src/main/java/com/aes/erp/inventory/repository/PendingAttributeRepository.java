package com.aes.erp.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.PendingAttribute;
import com.aes.erp.inventory.entity.PendingBrand;

@Repository
public interface PendingAttributeRepository extends JpaRepository<PendingAttribute,Long>{

    List<PendingAttribute> findAllBySubCategoryId(Long subCatId);

    
    
}
