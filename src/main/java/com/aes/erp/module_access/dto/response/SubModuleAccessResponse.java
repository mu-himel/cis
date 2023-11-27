package com.aes.erp.module_access.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubModuleAccessResponse extends ModuleAccessResponse{
    ParentModuleAccessResponse parentModuleAccess;
}
