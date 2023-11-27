package com.aes.erp.module_access.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeletePermissionRequest {
    private Long departmentId;
    private Long designationId;
    private Long userId;
}
