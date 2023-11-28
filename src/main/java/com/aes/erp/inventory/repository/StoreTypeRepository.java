package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.StoreType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreTypeRepository extends JpaRepository<StoreType, Long> {
}
