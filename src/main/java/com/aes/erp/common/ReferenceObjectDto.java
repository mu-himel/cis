package com.aes.erp.common;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReferenceObjectDto {
    
    public ReferenceObjectDto(Long id) {
        this.id = id;
    }

    private Long id;
}
