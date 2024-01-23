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
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;


@Data
@Entity
@EqualsAndHashCode(exclude = {"vendorSubCategories"})
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

    private LocalDate verificationDate;

    private LocalDate approvedDate;


    @Enumerated(EnumType.STRING)
    private VendorStatus status;


    @ManyToOne(fetch = FetchType.EAGER)
    private VendorType vendorType;

    private Integer aitPercentage;

    @OneToMany(mappedBy = "vendor",cascade = CascadeType.ALL)
    private List<VendorFile> files;

    @OneToOne(cascade = CascadeType.ALL)
    private User user;

    @OneToOne
    private ItemCategory category;

    @OneToMany(cascade = CascadeType.ALL)
    private List<VendorItem> vendorItems;

    @OneToOne(fetch = FetchType.LAZY)
    private DocumentHolder documentHolder;

    private Date startedAt;

    @UpdateTimestamp
    private Date completedAt;

    @OneToOne
    private VendorScore vendorScore;

    @OneToMany(mappedBy = "vendor", cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    private Set<VendorSubCategory> vendorSubCategories = new HashSet<>();
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