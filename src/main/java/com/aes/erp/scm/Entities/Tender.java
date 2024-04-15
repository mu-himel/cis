package com.aes.erp.scm.Entities;

import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tenders")
public class Tender {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private Long creationDate;

    private Long deadline;

    private String code;
    private String rfqNo;

    

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization tenderCreator;

    @OneToOne
    private ItemCategory itemCategory;

    @OneToMany(mappedBy = "tender", cascade = CascadeType.ALL)
    private List<TenderItem> tenderItems;

    @Enumerated(EnumType.STRING)
    private TenderStatus tenderStatus;

    @Enumerated(EnumType.STRING)
    private TenderType tenderType;

    @JsonIgnore
    @OneToMany(mappedBy = "tender", cascade = CascadeType.ALL)
    private List<Offer> offerList;

    public Tender(Long id) {
        this.id = id;
    }

    // public void setDeadline(String deadline){
    //     long d = Instant.parse(deadline).toEpochMilli();
    //     this.deadline = Long.valueOf(d);
    // }

    

    
}
