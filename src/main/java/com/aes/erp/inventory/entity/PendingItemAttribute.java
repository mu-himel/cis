package com.aes.erp.inventory.entity;

import javax.persistence.*;

import com.aes.erp.common.ReferenceObjectDto;
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
