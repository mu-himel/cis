package com.aes.erp.inventory.repository;

import com.aes.erp.inventory.entity.CategoryBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryBudgetRepository extends JpaRepository<CategoryBudget,Long> {
}
