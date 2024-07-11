package com.aes.erp.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(value = {"name"})
public class ReferenceObjectDto {
    
    public ReferenceObjectDto(Long id) {
        this.id = id;
    }

    private Long id;
}
