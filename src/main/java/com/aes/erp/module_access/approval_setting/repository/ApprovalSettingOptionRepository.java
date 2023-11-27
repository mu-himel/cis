package com.aes.erp.module_access.approval_setting.repository;

import com.aes.erp.module_access.approval_setting.entity.ApprovalSettingOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalSettingOptionRepository extends JpaRepository<ApprovalSettingOption,Long> {
}
