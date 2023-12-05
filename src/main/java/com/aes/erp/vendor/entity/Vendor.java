package com.aes.erp.vendor.entity;

import com.aes.erp.common.DtoConvertable;
import com.aes.erp.common.DtoConvertable;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.vendor.dto.VendorDto;
import com.aes.erp.vendor.entity.DocumentHolder.DocumentHolder;
import com.aes.erp.vendor.enums.VendorStatus;
import com.aes.erp.vendor.enums.VendorDocumentVerificationStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Vendor implements DtoConvertable<VendorDto> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String email;

    private String phone;

    @Enumerated(EnumType.STRING)
    private VendorDocumentVerificationStatus verificationStatus;


    @Enumerated(EnumType.STRING)
    private VendorStatus status;


    @ManyToOne(fetch = FetchType.EAGER)
    private VendorType vendorType;


    @OneToMany(mappedBy = "vendor",cascade = CascadeType.ALL)
    private List<VendorFile> files;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @ManyToOne
    private ItemCategory category;

    @ManyToOne
    private ItemCategory subCategory;

    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL)
    private List<VendorItem> vendorItems;
    @OneToOne(fetch = FetchType.LAZY)
    private DocumentHolder documentHolder;
    private Date startedAt;
    @Override
    @JsonIgnore
    public VendorDto getDto() {
        return new VendorDto();
    }

    public Vendor(Long id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }
}