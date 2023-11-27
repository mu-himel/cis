package com.aes.erp.module_access.approval_setting.entity;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSettingType;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSystemOperator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "approval_setting_options")
public class ApprovalSettingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore
    private ApprovalSetting approvalSetting;

    @Enumerated(EnumType.STRING)
    private ApprovalSystemOperator operator;

    @Enumerated(EnumType.STRING)
    private ApprovalSettingType approvalSettingType;

    private BigDecimal amount;


    @ManyToOne
    private ItemCategory category;


}
