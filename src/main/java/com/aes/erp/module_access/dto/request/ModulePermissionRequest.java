package com.aes.erp.module_access.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModulePermissionRequest {
    private Long userId;
    private String name;
    private Long departmentId;
    private Long designationId;
    private CrudPermission permission;
    private List<ModuleFilter> filters;
    private List<ModuleAccessVerifier> verifiers;
}
