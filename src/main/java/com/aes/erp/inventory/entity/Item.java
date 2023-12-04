package com.aes.erp.inventory.entity;

import com.aes.erp.inventory.enums.ItemUnit;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.user_management.entity.User;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@Table(name = "items")
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemCategory itemCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private ItemCategory itemParentCategory;

    @Column(unique = true, name = "code")
    private String code;

    private String name;

    public Item(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private String sku;

    private String manufacturer;

    @OneToMany(mappedBy = "item",cascade = CascadeType.ALL)
    @ApiModelProperty(hidden = true)
    private List<ItemStock> stocks;

    @Enumerated(EnumType.STRING)
    private ItemUnit itemUnit;

    private Integer stockThresholdQty;
    private Integer reorderPercentage;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemAttribute> attributes;

    private Boolean active=true;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ManyToOne
    private User createdBy;

}
