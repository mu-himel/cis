package com.aes.erp.indent.dto.request;

import com.aes.erp.demand.enums.DemandPriority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IndentDetailRequestDto {
    private IndentItemDto item;
    private Long prQty;
    private Long orderQty;
}
