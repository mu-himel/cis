package com.aes.erp.inventory.entity;

import com.aes.erp.vendor.entity.Vendor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
@Table(name = "subcategory_brands")
public class SubCategoryBrand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private ItemCategory subcategory;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
}
