package com.aes.erp.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aes.erp.inventory.entity.ItemAttribute;

@Repository
public interface ItemAttributeRepository extends JpaRepository<ItemAttribute, Long>{
    List<ItemAttribute> findAllByItemId(Long ItemId);
}
