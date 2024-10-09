package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Entity
@Data
@Table(name = "certificate_of_incorporation")
@EqualsAndHashCode(exclude = {"documentHolder"})

public class CertificateOfIncorporation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id", nullable = false)
    Long id;
    private String certificateOfIncorporationNo;
    private String companyName;
    @OneToOne
    private DocumentHolder documentHolder;
    @OneToOne
    private Document document;
    private Boolean enabledByDocumentHolder = false;

}
