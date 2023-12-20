package com.aes.erp.module_access.service;

import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.enums.ModuleType;
import com.aes.erp.module_access.repository.ModuleAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ModuleAccessServiceImpl implements ModuleAccessService{

    @Autowired
    private ModuleAccessRepository moduleAccessRepository;

    @Override
    public List<?> getAllModules() {
        return moduleAccessRepository.findAllOrderByDisplayOrderAsc();
    }

    @Override
    public List<ModuleAccess> getByParentModuleAccess(ModuleAccess moduleAccess) {
        return moduleAccessRepository.findByParentModuleAccess(moduleAccess);
    }

    @Override
    public Optional<ModuleAccess> getModuleAccessByUri(String uri) {
        return moduleAccessRepository.findByUri(uri);
    }


    @Override
    public void initModuleAccess() {
        List<ModuleAccess> moduleAccesses = new ArrayList<>();

        Long count = moduleAccessRepository.count();
        if(count==0) {

            ModuleAccess dashboard = new ModuleAccess();
            dashboard.setDisplayOrder(1);
            dashboard.setName("Dashboard");
            dashboard.setIcon("dashboard.svg");
            dashboard.setRoute("vendor-panel/documents-verification");
            dashboard.setUri("vendor-panel/documents-verification");
            dashboard.setModuleType(ModuleType.PARENT);
            dashboard.setShowInMenu(true);
            moduleAccessRepository.save(dashboard);

            ModuleAccess controlPanel = new ModuleAccess();
            controlPanel.setDisplayOrder(2);
            controlPanel.setName("Control Panel");
            controlPanel.setIcon("control_panel.svg");
            controlPanel.setRoute("control-panel");
            controlPanel.setUri("control-panel");
            controlPanel.setModuleType(ModuleType.PARENT);
            controlPanel.setShowInMenu(true);
            moduleAccessRepository.save(controlPanel);

            ModuleAccess organization = new ModuleAccess();
            organization.setDisplayOrder(3);
            organization.setName("Organization");
            organization.setRoute("control-panel/organization");
            organization.setUri("control-panel/organization");
            organization.setModuleType(ModuleType.CHILD);
            organization.setShowInMenu(true);
            organization.setParentModuleAccess(controlPanel);
            moduleAccessRepository.save(organization);

            ModuleAccess inventoryControl = new ModuleAccess();
            inventoryControl.setDisplayOrder(4);
            inventoryControl.setName("Inventory Control");
            inventoryControl.setRoute("control-panel/inventory-control");
            inventoryControl.setUri("control-panel/inventory-control");
            inventoryControl.setModuleType(ModuleType.CHILD);
            inventoryControl.setShowInMenu(true);
            inventoryControl.setParentModuleAccess(controlPanel);
            moduleAccessRepository.save(inventoryControl);

            ModuleAccess category = new ModuleAccess();
            category.setDisplayOrder(5);
            category.setName("Category");
            category.setRoute("control-panel/category");
            category.setUri("control-panel/category");
            category.setModuleType(ModuleType.CHILD);
            category.setShowInMenu(false);
            category.setParentModuleAccess(controlPanel);
            moduleAccessRepository.save(category);

            ModuleAccess subCategory = new ModuleAccess();
            subCategory.setDisplayOrder(6);
            subCategory.setName("Sub Category");
            subCategory.setRoute("control-panel/sub-category");
            subCategory.setUri("control-panel/sub-category");
            subCategory.setModuleType(ModuleType.CHILD);
            subCategory.setShowInMenu(false);
            subCategory.setParentModuleAccess(controlPanel);
            moduleAccessRepository.save(subCategory);

            ModuleAccess allPartner = new ModuleAccess();
            allPartner.setDisplayOrder(7);
            allPartner.setName("All Partner");
            allPartner.setRoute("verified-vendors");
            allPartner.setUri("verified-vendors");
            allPartner.setModuleType(ModuleType.CHILD);
            allPartner.setShowInMenu(true);
            allPartner.setParentModuleAccess(controlPanel);
            moduleAccessRepository.save(allPartner);

            ModuleAccess registration = new ModuleAccess();
            registration.setDisplayOrder(8);
            registration.setName("Registration");
            registration.setRoute("vendor-panel/vendors");
            registration.setUri("vendor-panel/vendors");
            registration.setModuleType(ModuleType.CHILD);
            registration.setShowInMenu(true);
            registration.setParentModuleAccess(controlPanel);
            moduleAccessRepository.save(registration);
        }
    }
}
