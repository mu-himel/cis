package com.aes.erp.scm.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"tender"})
@Table(name = "tender_items")
public class TenderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    private String productDescription;
    private String brandName;
    private String specification;
    private Long orderQuantity;
    @OneToMany(mappedBy = "tenderItem")
    private List<TenderDeliveryDetail> deliveryDetails = new ArrayList<>();
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "tender_id")
    private Tender tender;
}
