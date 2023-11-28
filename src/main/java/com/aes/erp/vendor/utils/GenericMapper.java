package com.aes.erp.vendor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class GenericMapper {
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;

    public GenericMapper(ObjectMapper objectMapper, ModelMapper modelMapper) {
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
    }

    public <T, U> U map(T source, Class<U> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public <T> T map(String responseToMap, Class<T> resultClass) throws Exception {
        return objectMapper.readValue(responseToMap, resultClass);
    }
}