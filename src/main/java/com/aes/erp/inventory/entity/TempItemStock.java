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
@Table(name = "temp_item_stocks")
public class TempItemStock {

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
    private TempItem item;

    public TempItemStock(Integer stockQty, TempItem item) {
        this.stockQty = stockQty;
        this.item = item;
    }

    public TempItemStock(Integer stockQty, TempItem item, StockType stockType) {
        this.stockQty = stockQty;
        this.item = item;
        this.stockType = stockType;
    }
}
