package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;

@Entity
@Data
@Table(name = "documents")
@EqualsAndHashCode(exclude = {"documentHolder"})
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String name;
    @Column(name = "document_type")
    private DocumentType documentType;
    @JsonIgnore
    @Lob
    private byte[] file;
    private String fileName;
    private String contentType;
    private String filePath;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_holder_id")
    private DocumentHolder documentHolder;
    @Column(columnDefinition = "TEXT")
    @Lob
    private String resultFromMachineLearning;
    private String confirmedByDocumentHolder;
}
