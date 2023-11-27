package com.aes.erp.inventory.entity;


import com.aes.erp.inventory.enums.StockType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;

import java.time.LocalDate;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "item_stocks")
public class ItemStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDate stockDate;

    private Integer stockQty;

    @Enumerated(EnumType.STRING)
    private StockType stockType;

    @ManyToOne
    @JsonIgnore
    private Item item;

    public ItemStock(Integer stockQty, Item item) {
        this.stockQty = stockQty;
        this.item = item;
    }

    public ItemStock(Integer stockQty, Item item, StockType stockType) {
        this.stockQty = stockQty;
        this.item = item;
        this.stockType = stockType;
    }
}
