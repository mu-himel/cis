package com.aes.erp.indent.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdatePrIndentDetailRequestDto {
    private Long id;
    @NotNull(message = "Pr Indent Details should not empty")
    private List<IndentDetailRecord> prIndentDetails;
}

