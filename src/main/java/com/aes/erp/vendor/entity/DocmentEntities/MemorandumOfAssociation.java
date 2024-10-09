package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@Table(name = "memorandum_of_association")
@EqualsAndHashCode(exclude = {"documentHolder"})

public class MemorandumOfAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    Long id;
    private String companyName;
    @OneToOne
    private DocumentHolder documentHolder;
    @OneToOne
    private Document document;
    private Boolean enabledByDocumentHolder = false;

}
