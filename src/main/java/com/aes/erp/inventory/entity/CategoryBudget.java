package com.aes.erp.inventory.entity;

import com.aes.erp.inventory.enums.BudgetType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@NoArgsConstructor
@Table(name = "category_budgets")
public class CategoryBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private ItemCategory category;

    private BigDecimal amount;

    private Integer currentYear;

    @Enumerated(EnumType.STRING)
    private BudgetType budgetType;

    public CategoryBudget(ItemCategory category, BigDecimal amount, Integer currentYear, BudgetType budgetType) {
        this.category = category;
        this.amount = amount;
        this.currentYear = currentYear;
        this.budgetType = budgetType;
    }
}
