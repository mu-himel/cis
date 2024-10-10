package com.aes.erp.vendor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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
        } catch (MismatchedInputException e) {
            String fieldName = e.getPathReference();
            String errorMessage = "Error converting JSON string to DTO. Mismatched field: " + fieldName;
            throw new RuntimeException(errorMessage, e);
        } catch (Exception e) {
            throw new RuntimeException("Error converting JSON string to DTO", e);
        }
    }
}
