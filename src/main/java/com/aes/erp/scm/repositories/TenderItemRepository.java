package com.aes.erp.scm.repositories;

import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface TenderItemRepository extends JpaRepository<TenderItem, Long> {

    List<TenderItem> findByTenderId(Long tender);
}
