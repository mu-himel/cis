package com.aes.erp.module_access.dto.response;

import com.aes.erp.module_access.enums.ModuleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAccessResponse {
    Long id;
    String name;
    ModuleType moduleType;
    String uri;
    String icon;
    String route;
    Boolean showInMenu;
    Integer displayOrder;
    List<SubModuleAccessResponse> children = new ArrayList<>();
    Boolean createPermission;
    Boolean readPermission;
    Boolean updatePermission;
    Boolean deletePermission;
}
