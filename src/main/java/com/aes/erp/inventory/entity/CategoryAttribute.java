package com.aes.erp.inventory.entity;

import com.aes.erp.inventory.enums.AttributeUnit;
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

    @Enumerated(EnumType.STRING)
    private AttributeUnit attributeUnit;

    @Column(length = 500)
    private String attributeValue;

    @ManyToOne
    @JsonIgnore
    private ItemCategory category;
}
