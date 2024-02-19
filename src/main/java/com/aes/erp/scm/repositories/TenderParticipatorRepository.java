package com.aes.erp.scm.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.scm.Entities.TenderParticipator;

@Repository
public interface TenderParticipatorRepository extends JpaRepository<TenderParticipator,Long>{

    Optional<TenderParticipator> findByTenderIdAndVendorId(Long id,Long vendorId);
    
}
