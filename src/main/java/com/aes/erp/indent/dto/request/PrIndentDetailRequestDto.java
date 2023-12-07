package com.aes.erp.indent.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrIndentDetailRequestDto {
    private IndentItemDto item;
    private Long prQty;
    private Long orderQty;
}
