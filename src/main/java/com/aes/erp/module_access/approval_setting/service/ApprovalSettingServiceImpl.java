package com.aes.erp.module_access.approval_setting.service;

import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.module_access.approval_setting.dto.request.ApprovalSettingDto;
import com.aes.erp.module_access.approval_setting.entity.ApprovalSetting;
import com.aes.erp.module_access.approval_setting.entity.ApprovalSettingOption;
import com.aes.erp.module_access.approval_setting.repository.ApprovalSettingOptionRepository;
import com.aes.erp.module_access.approval_setting.repository.ApprovalSettingQuery;
import com.aes.erp.module_access.approval_setting.repository.ApprovalSettingRepository;
import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.repository.ModuleAccessRepository;
import com.aes.erp.module_access.service.ModuleAccessService;
import com.aes.erp.organogram_system.entity.Department;
import com.aes.erp.organogram_system.entity.RoleNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApprovalSettingServiceImpl implements ApprovalSettingService{

    @Autowired
    private ApprovalSettingRepository approvalSettingRepository;

    @Autowired
    private ApprovalSettingOptionRepository approvalSettingOptionRepository;

    @Autowired
    private ModuleAccessService moduleAccessService;

    @Override
    @Transactional
    public void createApprovalSettings(List<ApprovalSettingDto> approvalSettingDto) {
        if(approvalSettingDto.size()>0) {
           List<ApprovalSetting> settings = approvalSettingDto.stream().map(_approvalSettingDto->{
                ApprovalSetting approvalSetting = new ApprovalSetting();
                BeanUtils.copyProperties(_approvalSettingDto, approvalSetting);


                if(_approvalSettingDto.getDepartment()!=null) {
                    approvalSetting.setDepartment(new Department(_approvalSettingDto.getDepartment().getId()));
                }

                if(_approvalSettingDto.getDesignation()!=null) {
                   approvalSetting.setDesignation(new RoleNode(_approvalSettingDto.getDesignation().getId()));
                }

                if(_approvalSettingDto.getModuleAccess()!=null) {
                    approvalSetting.setModuleAccess(new ModuleAccess(_approvalSettingDto.getModuleAccess().getId()));
                }

               approvalSetting.setApprovalSettingOptions(_approvalSettingDto.getApprovalSettingOptions().stream().map(aso -> {
                   ApprovalSettingOption approvalSettingOption = new ApprovalSettingOption();
                   if(aso.getId()!=null){
                       approvalSettingOption.setId(aso.getId());
                   }
                   approvalSettingOption.setApprovalSetting(approvalSetting);
                   approvalSettingOption.setApprovalSettingType(aso.getApprovalSettingType());
                   approvalSettingOption.setOperator(aso.getOperator());
                   approvalSettingOption.setAmount(aso.getAmount());
                   if(aso.getCategoryId()!=null) {
                       approvalSettingOption.setCategory(new ItemCategory(aso.getCategoryId()));
                   }
                   return approvalSettingOption;
               }).collect(Collectors.toList()));

                approvalSetting.setIsDefault(_approvalSettingDto.getIsDefault());
                return approvalSetting;
            }).collect(Collectors.toList());

            approvalSettingRepository.saveAll(settings);
        }
    }

    @Override
    public List<?> getDefaultApprovalSetting() {
        return approvalSettingRepository.findAllByIsDefault(true);
    }

    @Override
    public List<ApprovalSettingQuery.ApprovalPanel> getModuleWiseApprovalSetting(String uri,
                                                                                 Optional<Long> categoryId,
                                                                                 Optional<BigDecimal> amount) {
        List<Long> ids = null;
        if(categoryId.isPresent()){
            ids = new ArrayList<>();
            ids.add(categoryId.get());
        }
        return approvalSettingRepository.findByModuleAndOption(uri,ids,amount.orElse(null));
    }

    @Override
    public List<?> getModuleWiseApprovalSetting(String uri) {

        Optional<ModuleAccess> moduleAccessOp = moduleAccessService.getModuleAccessByUri(uri);
        if(moduleAccessOp.isPresent()){
            ModuleAccess moduleAccess = moduleAccessOp.get();
            List<ApprovalSettingRepository.DefaultApprovalSetting> settingList  = approvalSettingRepository.findByModuleAccess(moduleAccess);
            List<Map<String,Object>> settings = new ArrayList<>();
                settingList.stream().forEach(setting->{

                Map<String,Object> settingMap = new HashMap<>();
                settingMap.put("id",setting.getId());
                settingMap.put("isDefault",setting.getIsDefault());
                settingMap.put("designation",setting.getDesignation());
                settingMap.put("department",setting.getDepartment());
                settingMap.put("approvalOrder",setting.getApprovalOrder());
                settingMap.put("moduleAccess",setting.getModuleAccess());


                List<Map<String,Object>> optionMaps= new ArrayList<>();
                        setting.getApprovalSettingOptions().stream().forEach(approvalSettingOption -> {
                    Map<String,Object> optionMap = new HashMap<>();
                    optionMap.put("id",approvalSettingOption.getId());
                    optionMap.put("approvalSettingType",approvalSettingOption.getApprovalSettingType());
                    optionMap.put("amount",approvalSettingOption.getAmount());
                    optionMap.put("operator",approvalSettingOption.getOperator());
                    Long categoryId = approvalSettingOption.getCategory() !=null ? approvalSettingOption.getCategory().getId() : null;
                    String categoryName = approvalSettingOption.getCategory() !=null ? approvalSettingOption.getCategory().getName() : null;
                    optionMap.put("categoryId",categoryId);
                    optionMap.put("categoryName",categoryName);
                    optionMaps.add(optionMap);
                });
                    settingMap.put("approvalSettingOptions",optionMaps);
                    settings.add(settingMap);
            });
            return settings;
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional
    public void deleteApprovalSetting(Long id) {
        approvalSettingRepository.deleteById(id);
    }

    @Override
    public void deleteApprovalSettingOption(Long id) {
        approvalSettingOptionRepository.deleteById(id);
    }
}
