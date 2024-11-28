package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name = "bank_solvency")
@EqualsAndHashCode(exclude = "documentHolder")
public class BankSolvencyDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String account;
    private String accountHolderName;
    @OneToOne(fetch = FetchType.LAZY)
    private DocumentHolder documentHolder;
    @OneToOne(fetch = FetchType.LAZY)
    private Document document;
    private Boolean enabledByDocumentHolder = false;
    private String branchName;
    private String bankName;
    private String routingNumber;
}
