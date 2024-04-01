package com.aes.erp.inventory.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
@Entity
@Table(name = "pending_item_attributes")
public class PendingItemAttribute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String attributeType;

    private String attributeValue;

    private String attributeUnit;

    @ManyToOne
    @JsonIgnore
    private PendingItemRequest pendingItemRequest;
}
