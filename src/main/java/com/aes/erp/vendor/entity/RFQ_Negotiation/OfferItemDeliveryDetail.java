package com.aes.erp.vendor.entity.RFQ_Negotiation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offer_item_delivery_details")
public class OfferItemDeliveryDetail {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(updatable = false)
   private Long id;
   private String itemName;
   private BigDecimal deliveryOrderQTY;

   private LocalDate deliveryDate;

   @ManyToOne
   @JsonIgnore
   private OfferDeliveryDetail offerDeliveryDetail;
}
