package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "business_details")
@Data
public class BusinessDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String orgName;
    private String businessType;
    private String numberOfYear;
    private String annualVolume;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentHolder documentHolder;
}
