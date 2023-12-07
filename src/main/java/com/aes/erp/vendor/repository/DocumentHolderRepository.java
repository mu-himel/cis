package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentHolderRepository extends JpaRepository<DocumentHolder, Long> {
}
