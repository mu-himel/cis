package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.VendorFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorFileRepository extends JpaRepository<VendorFile,Long> {
    Optional<VendorFile> findByVendorIdAndBusinessDetailsIdAndFileName(Long id, Long businessDetailId, String filename);
}
