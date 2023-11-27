package com.aes.erp.module_access.approval_setting.service;

import com.aes.erp.module_access.approval_setting.dto.request.ApprovalSettingDto;
import com.aes.erp.module_access.approval_setting.entity.ApprovalSetting;
import com.aes.erp.module_access.approval_setting.repository.ApprovalSettingQuery;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ApprovalSettingService {

    void createApprovalSettings(List<ApprovalSettingDto> approvalSettingDto);

    List<?> getDefaultApprovalSetting();
    List<ApprovalSettingQuery.ApprovalPanel> getModuleWiseApprovalSetting(String uri, Optional<Long> categoryId, Optional<BigDecimal> amount);
    List<?> getModuleWiseApprovalSetting(String uri);

    void deleteApprovalSetting(Long id);

    void deleteApprovalSettingOption(Long id);
}
