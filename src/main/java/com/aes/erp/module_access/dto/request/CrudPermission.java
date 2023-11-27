package com.aes.erp.module_access.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrudPermission {

    private Boolean createPermission;
    private Boolean readPermission;
    private Boolean deletePermission;
    private Boolean updatePermission;
}
