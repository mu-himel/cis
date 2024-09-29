package com.aes.erp.inventory.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "item_functional_units")
@Data
public class ItemFunctionalUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal value;

    private String unit;

    @ManyToOne()
    @JsonIgnore
    private Item item;
}
