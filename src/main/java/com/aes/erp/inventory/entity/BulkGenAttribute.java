package com.aes.erp.inventory.entity;

import java.util.List;

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
@Table(name = "bulk_gen_attributes")
public class BulkGenAttribute {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String attributeType;
    private String attributeUnit;
    private String attributeValue;

    @ManyToOne
    @JsonIgnore
    private BulkItemGenConfig bulkItemGenConfig;

    

}
