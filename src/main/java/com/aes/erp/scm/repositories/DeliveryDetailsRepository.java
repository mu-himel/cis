package com.aes.erp.scm.repositories;

import com.aes.erp.scm.Entities.TenderDeliveryDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryDetailsRepository extends JpaRepository<TenderDeliveryDetail, Long> {
}
