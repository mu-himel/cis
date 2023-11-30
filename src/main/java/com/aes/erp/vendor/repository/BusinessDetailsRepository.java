package com.aes.erp.vendor.repository;

import com.aes.erp.vendor.entity.DocmentEntities.BusinessDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessDetailsRepository extends JpaRepository<BusinessDetails, Long>{

}