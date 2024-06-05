package com.aes.erp.inventory.entity;

import com.aes.erp.inventory.enums.ItemUnit;
import com.aes.erp.user_management.entity.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Entity
@Table(name = "temp_items")
@NoArgsConstructor
@AllArgsConstructor
public class TempItem {

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

    private String itemAttributeName;

    private Boolean isSyncronized;

    public TempItem(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    private String sku;

    private String manufacturer;

    @ManyToOne
    private Brand brand;

    @ManyToOne
    private StoreType storeType;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    @ApiModelProperty(hidden = true)
    private List<TempItemStock> stocks;

    private String itemUnit;

    private Integer stockThresholdQty;
    private Integer reorderPercentage;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<TempItemAttribute> attributes;

    private Boolean active=true;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using=LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

    @ManyToOne
    private User createdBy;

}
