package com.aes.erp.module_access.service;

import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.enums.ModuleType;
import com.aes.erp.module_access.repository.ModuleAccessRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Transactional
    public Long initModuleAccess() {
        List<ModuleAccess> moduleAccesses = new ArrayList<>();

        Long count = moduleAccessRepository.count();
        if(count==0) {

            ModuleAccess dashboard = new ModuleAccess();
            dashboard.setDisplayOrder(1);
            dashboard.setId(2L);
            dashboard.setName("Dashboard");
            dashboard.setIcon("dashboard.svg");
            dashboard.setRoute("vendor-panel/documents-verification");
            dashboard.setUri("vendor-panel/documents-verification");
            dashboard.setModuleType(ModuleType.PARENT);
            dashboard.setShowInMenu(true);
            moduleAccessRepository.save(dashboard);

            ModuleAccess controlPanel = new ModuleAccess();
            controlPanel.setId(3L);
            controlPanel.setDisplayOrder(2);
            controlPanel.setName("Control Panel");
            controlPanel.setIcon("control_panel.svg");
            controlPanel.setRoute("control-panel");
            controlPanel.setUri("control-panel");
            controlPanel.setModuleType(ModuleType.PARENT);
            controlPanel.setShowInMenu(true);

            ModuleAccess organization = new ModuleAccess();
            organization.setId(4L);
            organization.setDisplayOrder(3);
            organization.setName("Organization");
            organization.setRoute("control-panel/organization");
            organization.setUri("control-panel/organization");
            organization.setModuleType(ModuleType.CHILD);
            organization.setShowInMenu(true);
            organization.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(organization);

            ModuleAccess inventoryControl = new ModuleAccess();
            inventoryControl.setId(5L);
            inventoryControl.setDisplayOrder(4);
            inventoryControl.setName("Inventory Control");
            inventoryControl.setRoute("control-panel/inventory-control");
            inventoryControl.setUri("control-panel/inventory-control");
            inventoryControl.setModuleType(ModuleType.CHILD);
            inventoryControl.setShowInMenu(true);
            inventoryControl.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(inventoryControl);

            ModuleAccess category = new ModuleAccess();
            category.setId(6L);
            category.setDisplayOrder(5);
            category.setName("Category");
            category.setRoute("control-panel/category");
            category.setUri("control-panel/category");
            category.setModuleType(ModuleType.CHILD);
            category.setShowInMenu(false);
            category.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(category);

            ModuleAccess subCategory = new ModuleAccess();
            subCategory.setId(7L);
            subCategory.setDisplayOrder(6);
            subCategory.setName("Sub Category");
            subCategory.setRoute("control-panel/sub-category");
            subCategory.setUri("control-panel/sub-category");
            subCategory.setModuleType(ModuleType.CHILD);
            subCategory.setShowInMenu(false);
            subCategory.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(subCategory);

            ModuleAccess allPartner = new ModuleAccess();
            allPartner.setId(8L);
            allPartner.setDisplayOrder(7);
            allPartner.setName("All Partner");
            allPartner.setRoute("vendor-panel/all-partners/approved-vendor");
            allPartner.setUri("vendor-panel/all-partners/approved-vendor");
            allPartner.setModuleType(ModuleType.CHILD);
            allPartner.setShowInMenu(true);
            allPartner.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(allPartner);

            ModuleAccess registration = new ModuleAccess();
            registration.setId(9L);
            registration.setDisplayOrder(8);
            registration.setName("Registration");
            registration.setRoute("vendor-panel/registration/vendor");
            registration.setUri("vendor-panel/registration/vendor");
            registration.setModuleType(ModuleType.CHILD);
            registration.setShowInMenu(true);
            registration.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(registration);

            ModuleAccess employee = new ModuleAccess();
            employee.setId(10L);
            employee.setDisplayOrder(9);
            employee.setName("Employee");
            employee.setRoute("vendor-panel/registration/employee");
            employee.setUri("vendor-panel/registration/employee");
            employee.setModuleType(ModuleType.CHILD);
            employee.setShowInMenu(true);
            employee.setParentModuleAccess(controlPanel);
            controlPanel.addChildModule(employee);
            moduleAccessRepository.save(controlPanel);

            ModuleAccess vendorManagement = new ModuleAccess();
            vendorManagement.setId(11L);
            vendorManagement.setDisplayOrder(10);
            vendorManagement.setName("Vendor Management");
            vendorManagement.setIcon("vendor_management.svg");
            vendorManagement.setRoute("vendor-management");
            vendorManagement.setUri("vendor-management");
            vendorManagement.setModuleType(ModuleType.PARENT);
            vendorManagement.setShowInMenu(true);

            ModuleAccess pendingVendors = new ModuleAccess();
            pendingVendors.setId(12L);
            pendingVendors.setDisplayOrder(11);
            pendingVendors.setName("Pending Vendors");
            pendingVendors.setRoute("vendor-panel/pending-vendors");
            pendingVendors.setUri("vendor-panel/pending-vendors");
            pendingVendors.setModuleType(ModuleType.CHILD);
            pendingVendors.setShowInMenu(true);
            pendingVendors.setParentModuleAccess(vendorManagement);
            vendorManagement.addChildModule(pendingVendors);

            ModuleAccess pendingVerification = new ModuleAccess();
            pendingVerification.setId(13L);
            pendingVerification.setDisplayOrder(12);
            pendingVerification.setName("Pending Verification");
            pendingVerification.setRoute("vendor-panel/pending-vendors/pending-verification");
            pendingVerification.setUri("vendor-panel/pending-vendors/pending-verification");
            pendingVerification.setModuleType(ModuleType.CHILD);
            pendingVerification.setShowInMenu(false);
            pendingVerification.setParentModuleAccess(vendorManagement);
            vendorManagement.addChildModule(pendingVerification);

            ModuleAccess pendingApproval = new ModuleAccess();
            pendingApproval.setId(14L);
            pendingApproval.setDisplayOrder(13);
            pendingApproval.setName("Pending Approval");
            pendingApproval.setRoute("vendor-panel/pending-vendors/pending-approval");
            pendingApproval.setUri("vendor-panel/pending-vendors/pending-approval");
            pendingApproval.setModuleType(ModuleType.CHILD);
            pendingApproval.setShowInMenu(false);
            pendingApproval.setParentModuleAccess(vendorManagement);
            vendorManagement.addChildModule(pendingApproval);

            ModuleAccess approvedVendor = new ModuleAccess();
            approvedVendor.setId(15L);
            approvedVendor.setDisplayOrder(14);
            approvedVendor.setName("Complete");
            approvedVendor.setRoute("vendor-panel/pending-vendors/completed");
            approvedVendor.setUri("vendor-panel/pending-vendors/completed");
            approvedVendor.setModuleType(ModuleType.CHILD);
            approvedVendor.setShowInMenu(false);
            approvedVendor.setParentModuleAccess(vendorManagement);
            vendorManagement.addChildModule(approvedVendor);
            moduleAccessRepository.save(vendorManagement);

            ModuleAccess tenderModule = new ModuleAccess();
            tenderModule.setId(16L);
            tenderModule.setDisplayOrder(15);
            tenderModule.setName("Tender");
            tenderModule.setIcon("tender.svg");
            tenderModule.setRoute("tenders");
            tenderModule.setUri("tenders");
            tenderModule.setModuleType(ModuleType.PARENT);
            tenderModule.setShowInMenu(true);

            ModuleAccess rfq = new ModuleAccess();
            rfq.setId(17L);
            rfq.setDisplayOrder(16);
            rfq.setName("Rfq");
            rfq.setRoute("tenders/rfq/pending");
            rfq.setUri("tenders/rfq/pending");
            rfq.setModuleType(ModuleType.CHILD);
            rfq.setShowInMenu(false);
            rfq.setParentModuleAccess(tenderModule);
            tenderModule.addChildModule(rfq);

            ModuleAccess pendingRfq = new ModuleAccess();
            pendingRfq.setId(18L);
            pendingRfq.setDisplayOrder(17);
            pendingRfq.setName("Pending Rfq");
            pendingRfq.setRoute("tenders/rfq/pending");
            pendingRfq.setUri("tenders/rfq/pending");
            pendingRfq.setModuleType(ModuleType.CHILD);
            pendingRfq.setShowInMenu(false);
            pendingRfq.setParentModuleAccess(tenderModule);
            tenderModule.addChildModule(pendingRfq);

            ModuleAccess closedRfq = new ModuleAccess();
            closedRfq.setId(19L);
            closedRfq.setDisplayOrder(18);
            closedRfq.setName("Closed Rfq");
            closedRfq.setRoute("tenders/rfq/closed");
            closedRfq.setUri("tenders/rfq/closed");
            closedRfq.setModuleType(ModuleType.CHILD);
            closedRfq.setShowInMenu(false);
            closedRfq.setParentModuleAccess(tenderModule);
            tenderModule.addChildModule(closedRfq);

            ModuleAccess po = new ModuleAccess();
            po.setId(20L);
            po.setDisplayOrder(19);
            po.setName("PO");
            po.setRoute("tenders/po");
            po.setUri("tenders/po");
            po.setModuleType(ModuleType.CHILD);
            po.setShowInMenu(true);
            po.setParentModuleAccess(tenderModule);
            tenderModule.addChildModule(po);

            ModuleAccess pendingPo = new ModuleAccess();
            pendingPo.setId(21L);
            pendingPo.setDisplayOrder(20);
            pendingPo.setName("Pending PO");
            pendingPo.setRoute("tenders/po/pending");
            pendingPo.setUri("tenders/po/pending");
            pendingPo.setModuleType(ModuleType.CHILD);
            pendingPo.setShowInMenu(true);
            pendingPo.setParentModuleAccess(tenderModule);
            tenderModule.addChildModule(pendingPo);

            ModuleAccess closedPo = new ModuleAccess();
            closedPo.setId(22L);
            closedPo.setDisplayOrder(21);
            closedPo.setName("Closed PO");
            closedPo.setRoute("tenders/po/closed");
            closedPo.setUri("tenders/po/closed");
            closedPo.setModuleType(ModuleType.CHILD);
            closedPo.setShowInMenu(true);
            closedPo.setParentModuleAccess(tenderModule);
            tenderModule.addChildModule(closedPo);
            
            moduleAccessRepository.save(tenderModule);

        }
        return count;
    }
}
