package com.aes.erp.vendor.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "vendor_types")
public class VendorType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20)
    private String name;
}
