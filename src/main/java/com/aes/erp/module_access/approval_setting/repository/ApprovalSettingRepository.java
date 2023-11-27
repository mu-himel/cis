package com.aes.erp.module_access.approval_setting.repository;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.module_access.approval_setting.entity.ApprovalSetting;
import com.aes.erp.module_access.approval_setting.entity.ApprovalSettingOption;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSettingType;
import com.aes.erp.module_access.approval_setting.enums.ApprovalSystemOperator;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.organogram_system.repository.DepartmentRepository;
import com.aes.erp.organogram_system.repository.RoleNodeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ApprovalSettingRepository extends JpaRepository<ApprovalSetting,Long>, ApprovalSettingQuery {

    List<DefaultApprovalSetting> findAllByIsDefault(boolean b);

    @Query(value = getApprovalSettingModuleWise,nativeQuery = true)
    List<ApprovalPanel> findByModuleAndOption(
            @Param("uri") String uri,
            @Param("categoryId") List<Long> categoryId,
            @Param("amount") BigDecimal amount);

    List<DefaultApprovalSetting> findByModuleAccess(ModuleAccess moduleAccess);

    interface DefaultApprovalSetting{
        Long getId();
        DepartmentRepository.DepartmentInfo getDepartment();
        RoleNodeRepository.RoleNodeInfo getDesignation();
        ModuleAccessInfo getModuleAccess();

        Integer getApprovalOrder();
        Boolean getIsDefault();
        List<ApprovalSettingOption> getApprovalSettingOptions();
    }
    interface ModuleAccessInfo{
        Long getId();
        String getName();
        String getUri();
    }
}
