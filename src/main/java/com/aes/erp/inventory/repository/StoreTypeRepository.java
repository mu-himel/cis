package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.StoreType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreTypeRepository extends JpaRepository<StoreType, Long> {
    @Query(value = "SELECT * FROM store_types st WHERE st.active=1 AND (:name IS NULL OR st.name LIKE %:name%)",
            countQuery = "SELECT count(*) FROM store_types st WHERE st.active=1 AND (:name IS NULL OR st.name LIKE %:name%)",
            nativeQuery = true)
    Page<StoreTypeExt> getAllStoreTypes(Pageable pageable, @Param("name") String name);
    StoreType getStoreTypeByName(String name);
    public interface StoreTypeExt{
        Long getId();
        String getName();
    }
}
