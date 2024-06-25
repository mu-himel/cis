package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.AttributeUnit;
import com.aes.erp.inventory.entity.CategoryAttribute;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeUnitRepository extends JpaRepository<AttributeUnit,Long> {

    List<AttributeUnit> findAllByOrderByNameAsc();
}
