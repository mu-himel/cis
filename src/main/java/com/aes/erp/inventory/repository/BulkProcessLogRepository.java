package com.aes.erp.inventory.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.BulkProcessLog;

@Repository
public interface BulkProcessLogRepository extends JpaRepository<BulkProcessLog,Long>{

    Optional<BulkProcessLog> findFirstByProcessNameOrderByIdDesc(String name);

    Optional<BulkProcessLog> findByBulkItemGenConfigId(Long id);    
}
