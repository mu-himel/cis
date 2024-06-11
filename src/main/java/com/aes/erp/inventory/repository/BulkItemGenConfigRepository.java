package com.aes.erp.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.BulkItemGenConfig;

@Repository
public interface BulkItemGenConfigRepository  extends JpaRepository<BulkItemGenConfig,Long>{

    Optional<BulkItemGenConfig> findByConfigHash(String configHash);
    
}
