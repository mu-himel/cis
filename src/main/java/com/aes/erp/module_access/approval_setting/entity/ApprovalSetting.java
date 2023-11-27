package com.aes.erp.module_access.approval_setting.entity;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSettingType;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSystemOperator;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessPermission;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "approval_settings")
public class ApprovalSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Department department;

    @ManyToOne
    private RoleNode designation;

    @ManyToOne
    private ModuleAccess moduleAccess;

    private Integer approvalOrder;


    private Boolean isDefault;

    @OneToMany(mappedBy = "approvalSetting",cascade = CascadeType.ALL)
    private List<ApprovalSettingOption> approvalSettingOptions;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
