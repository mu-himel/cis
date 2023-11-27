package com.aes.erp.module_access.dto.request;

import com.aes.erp.common.ReferenceObjectDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModuleFilter {
    private String columnName;
    private List<FilterColumnValue> columnValues;
}
