package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.AttributeUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttributeUnitRepository extends JpaRepository<AttributeUnit,Long> {
}
