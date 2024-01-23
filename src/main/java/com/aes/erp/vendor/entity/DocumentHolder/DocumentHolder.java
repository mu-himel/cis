package com.aes.erp.vendor.entity.DocumentHolder;

import com.aes.erp.vendor.entity.DocmentEntities.*;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private Set<Document> documentList = new HashSet<>();
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
    @OneToMany(mappedBy = "documentHolder")
    private List<BusinessDetails> businessDetailsRecords;
    @OneToOne
    private GeneralDetails generalDetails;
    @Enumerated(EnumType.STRING)
    private DocumentHolderStatus documentHolderStatus = DocumentHolderStatus.CREATED;

    public void addDocument(Document document) {
        if (document != null) {
            documentList.add(document);
            document.setDocumentHolder(this);
        }
    }
    public void removeDocument(Document document) {
        if (document != null) {
            documentList.remove(document);
            document.setDocumentHolder(null); // Remove the reference to this DocumentHolder
        }
    }

}
