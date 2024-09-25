package com.aes.erp.scm.dto.remote;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class ReferenceObjDto {
    public ReferenceObjDto(Long id) {
        this.id = id;
    }

    private Long id;
}
