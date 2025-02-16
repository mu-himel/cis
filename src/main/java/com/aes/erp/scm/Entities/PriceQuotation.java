package com.aes.erp.scm.Entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "price_quotations")
public class PriceQuotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Column(precision = 38, scale = 4)
    private BigDecimal pricePerUnit;

    @Column(precision = 38, scale = 4)
    private BigDecimal totalPrice;
}
