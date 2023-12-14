package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Data
@Entity
@Table(name = "trade_license")
public class TradeDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private Timestamp issueDate;
    private String mobileNo;
    private String nid;
    private String tradeLicenseNumber;
    @OneToOne
    private DocumentHolder documentHolder;
    @OneToOne
    private Document document;
    private Boolean enabledByDocumentHolder = false;
}
