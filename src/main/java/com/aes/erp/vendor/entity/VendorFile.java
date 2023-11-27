package com.aes.erp.vendor.entity;

import com.aes.erp.vendor.enums.VendorDocType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.DocumentType;

import javax.persistence.*;
import java.util.Map;
import java.util.Optional;

@Entity
@Data
@Table(name = "vendor_files")
@NoArgsConstructor
public class VendorFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    private String fileName;

    // relative path
    private String path;

    private String size;

    private String mimeType;

    @Column(columnDefinition = "json",nullable = true)
    private String ocrData;

    // optional
    // what is inside document, line nid or
    private VendorDocType documentType;


    public void setOcrData(Map<String,Object> ocrData)  {
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.ocrData = (ocrData!=null)? mapper.writeValueAsString(ocrData) : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Map<String,Object>> getOcrData() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String,Object> _ocrData = null;
        if(ocrData != null && !ocrData.isBlank() && !ocrData.isEmpty()){
            try {
                _ocrData = mapper.readValue(ocrData,Map.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);

            }
        }
        return Optional.ofNullable(_ocrData);
    }

    public VendorFile(Vendor vendor, String fileName, String path, String size, String mimeType, VendorDocType documentType) {
        this.vendor = vendor;
        this.fileName = fileName;
        this.path = path;
        this.size = size;
        this.mimeType = mimeType;
        this.documentType = documentType;
    }
}
