package com.aes.erp.module_access.dto.request;

import com.aes.erp.module_access.entity.ModuleAccessFilter;
import com.aes.erp.module_access.enums.ModuleAssignType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModulePermissionRequest {
    ModuleAssignType moduleAssignType;
    Long userId;
    Long designationId;
    Long departmentId;
    @NotNull(message = "Create Permission missing")
    Boolean createPermission;
    @NotNull(message = "Update Permission missing")
    Boolean updatePermission;
    @NotNull(message = "Read Permission missing")
    Boolean readPermission;
    @NotNull(message = "Delete Permission missing")
    Boolean deletePermission;
    List<ModuleAccessFilter> filters;
}
