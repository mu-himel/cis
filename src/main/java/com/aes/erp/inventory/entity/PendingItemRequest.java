package com.aes.erp.inventory.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import lombok.Data;

@Data
@Entity
@Table(name = "pending_item_requests")
public class PendingItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestNo;

    @Column(length = 2000)
    private String requestedBy;

    @ManyToOne
    private ItemCategory category;

    @ManyToOne
    private ItemCategory subCategory;

    @ManyToOne
    private Brand brand; 

    private String employeeId;
    private String reportingManager;
    private String designation;
    private String department;
    private Long warehouseId;
    private Long warehouseStoreId;

    private String warehouseName;
    private String warehouseLocation;
    private String itemAttributeName;
    private String extendedAttributes;

    private String itemUnit;

    private String code;
    private Long scmItemId;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "pendingItemRequest", cascade = CascadeType.ALL)
    private List<PendingItemAttribute> attributes;

    @CreationTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedAt;

}