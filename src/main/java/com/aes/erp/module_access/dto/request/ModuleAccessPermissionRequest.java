package com.aes.erp.module_access.dto.request;

import com.aes.erp.module_access.enums.ModuleAssignType;
import com.aes.erp.module_access.enums.ModuleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAccessPermissionRequest {

    private Long id;
    private String name;
    private ModuleType moduleType;
    private ModuleAssignType moduleAssignType;
    private Long parentId;
    private String parentName;
    private List<ModulePermissionRequest> modulePermissions;

}
