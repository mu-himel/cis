package com.aes.erp.vendor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericObjectMapper {
    public GenericObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    private final ObjectMapper objectMapper;

    public <D> D convertStringToDto(String jsonString, Class<D> dtoClass) {
        try {
            return objectMapper.readValue(jsonString, dtoClass);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to DTO", e);
        }
    }
}
