package com.aes.erp.vendor.entity;

import com.aes.erp.inventory.entity.Item;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vendor_items")
public class VendorItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Item item;

    @ManyToOne
    @JsonIgnore
    private Vendor vendor;

    public VendorItem(Item item, Vendor vendor) {
        this.item = item;
        this.vendor = vendor;
    }
}
