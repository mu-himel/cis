package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "bin")
@Data
public class BINDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String address;
    private String bin;
    private Date effectiveDate;
    private Date issueDate;
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
