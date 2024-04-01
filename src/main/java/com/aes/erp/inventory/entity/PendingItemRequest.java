package com.aes.erp.inventory.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Data
@Entity
@Table(name = "pending_item_requests")
public class PendingItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String requestNo;

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
    private String warehouseName;
    private String warehouseLocation;

    @OneToMany(mappedBy = "pendingItemRequest", cascade = CascadeType.ALL)
    private List<PendingItemAttribute> attributes;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}