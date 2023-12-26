package com.aes.erp.vendor.entity.DocmentEntities;

import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.entity.VendorFile;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "business_details")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"documentHolder"})
@AllArgsConstructor
public class BusinessDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String orgName;
    private String businessType;
    private String numberOfYear;
    private String annualVolume;
    @Lob
    private String workOrderFile;
    @JsonIgnore
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "document_holder_id")
    private DocumentHolder documentHolder;


    public BusinessDetails(Long id) {
        this.id = id;
    }
}
