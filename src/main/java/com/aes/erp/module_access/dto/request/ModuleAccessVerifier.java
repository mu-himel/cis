package com.aes.erp.module_access.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleAccessVerifier {
    private String criteriaGroup;

    private String criteriaColumn;

    private String criteriaIdValue;
    private String criteriaText;
    private Integer level;
}
