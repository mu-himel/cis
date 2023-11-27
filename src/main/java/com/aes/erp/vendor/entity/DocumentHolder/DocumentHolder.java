package com.aes.erp.vendor.entity.DocumentHolder;

import com.aes.erp.vendor.entity.DocmentEntities.*;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "document_holders")
@Data
public class DocumentHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "documentHolder")
    private List<Document> documentList;
    @OneToOne
    private NIDDocument nidDocument;
    @OneToOne
    private BankSolvencyDocument bankSolvencyDocument;
    @OneToOne
    private TINDocument tinDocument;
    @OneToOne
    private BINDocument binDocument;
    @OneToOne
    private TradeDocument tradeDocument;
    @OneToOne
    private BusinessDetails businessDetails;
    @OneToOne
    private GeneralDetails generalDetails;
//    private String resumeNumber;


}
