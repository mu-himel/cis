package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.w3c.dom.Text;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bin")
@EqualsAndHashCode(exclude = "documentHolder")
@Data
public class BINDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String address;
    private String bin;
    @Column(columnDefinition = "TEXT")
    private String effectiveDate;
    private Timestamp issueDate;
    private String companyName;
    private String oldBin;
    private String tin;
    private String ownershipType;
    @OneToOne(fetch = FetchType.LAZY)
    private DocumentHolder documentHolder;
    @OneToOne(fetch = FetchType.LAZY)
    private Document document;
    private Boolean enabledByDocumentHolder = false;
//    @JsonProperty("all_info")

//    private List<String> allInformation;
}
