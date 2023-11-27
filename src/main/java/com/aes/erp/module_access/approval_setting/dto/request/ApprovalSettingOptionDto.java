package com.aes.erp.module_access.approval_setting.dto.request;

import com.aes.erp.module_access.approval_setting.entity.ApprovalSetting;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSettingType;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSystemOperator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalSettingOptionDto {

    private Long id;


    private ApprovalSystemOperator operator;


    private ApprovalSettingType approvalSettingType;

    private BigDecimal amount;

    private Long categoryId;
}
