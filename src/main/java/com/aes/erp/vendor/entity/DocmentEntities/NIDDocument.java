package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
@Data
@Entity
@Table(name = "nid")
@EqualsAndHashCode(exclude = "documentHolder")
public class NIDDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String dateOfBirth;
    private String name;
    private String nid;
    private String fatherName;
    private String motherName;
    private String banglaName;

    @OneToOne
    private DocumentHolder documentHolder;
    @OneToOne
    private Document document;
    private Boolean enabledByDocumentHolder = false;
}
