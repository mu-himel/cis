package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.VendorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorTypeRepository extends JpaRepository<VendorType,Long> {


}
