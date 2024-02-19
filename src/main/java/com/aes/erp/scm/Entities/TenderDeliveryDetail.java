package com.aes.erp.scm.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"tenderItem"})
@Table(name = "tender_delivery_details")
public class TenderDeliveryDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private Long warehouseId;
    private String wareHouseName;
    private String wareHouseAddress;
    private Long deliveryOrderQTY;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    private TenderItem tenderItem;
}
