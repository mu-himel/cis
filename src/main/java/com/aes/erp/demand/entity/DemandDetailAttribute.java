package com.aes.erp.demand.entity;

import com.aes.erp.inventory.enums.AttributeUnit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="demand_detail_attributes")
public class DemandDetailAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private DemandDetail demandDetail;

    private String attributeType;

    @Enumerated(EnumType.STRING)
    private AttributeUnit attributeUnit;

    @Column(length = 500)
    private String attributeValue;
}
