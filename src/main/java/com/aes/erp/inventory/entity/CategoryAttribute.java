package com.aes.erp.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@Table(name = "category_attributes")
@EqualsAndHashCode(exclude = {"category"})
public class CategoryAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attributeType;

    // @Enumerated(EnumType.STRING)
    private String attributeUnit;

    @Column(length = 500)
    private String attributeValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private ItemCategory category;
}
