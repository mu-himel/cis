package com.aes.erp.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "item_attributes")
@Data
public class ItemAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attributeType;

    private String attributeUnit;

    @Column(length = 500)
    private String attributeValue;

    @ManyToOne
    @JsonIgnore
    private Item item;
}
