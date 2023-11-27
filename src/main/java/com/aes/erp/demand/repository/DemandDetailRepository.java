package com.aes.erp.demand.repository;

import com.aes.erp.demand.entity.DemandDetail;
import com.aes.erp.demand.enums.DemandItemStatus;
import com.aes.erp.demand.enums.DemandStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandDetailRepository extends JpaRepository<DemandDetail,Long> {
    Integer countByStatus(DemandItemStatus pending);

    Integer countByStatusAndDemandId(DemandStatus pendingQc, Long id);
}
