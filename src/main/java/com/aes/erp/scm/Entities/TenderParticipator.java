package com.aes.erp.scm.Entities;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.aes.erp.vendor.entity.Vendor;

import lombok.Data;

@Data
@Entity
@Table(name = "tender_participators")
public class TenderParticipator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Vendor vendor;

    @Enumerated(EnumType.STRING)
    private TenderStatus status;

    @ManyToOne
    private Tender tender;
}
