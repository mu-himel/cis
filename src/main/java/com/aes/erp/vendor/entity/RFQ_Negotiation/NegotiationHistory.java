package com.aes.erp.vendor.entity.RFQ_Negotiation;

import com.aes.erp.scm.Entities.Tender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "negotiation_history")
public class NegotiationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;
    @OneToMany(mappedBy = "negotiationHistory", cascade = CascadeType.ALL)
    private List<Offer> offers;
    @OneToOne
    private Tender tender;
}

