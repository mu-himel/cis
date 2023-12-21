package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "general_details")
@Data
public class GeneralDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String modeOfTransaction;
    private String creditPeriodDays;
    private String deliverySchedule;
    private String replacementType;
    private String modeOfTransportation;
    private String urgentDeliverySupport;
    private String annualBusinessVolume;
    private String deliveryLeadTime;
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private DocumentHolder documentHolder;
}
