package com.aes.erp.vendor.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class GenericModelMapper {
    @Bean
    public ModelMapper modelMapper() {
        return modelMapper;
    }
    private final ModelMapper modelMapper;
    public GenericModelMapper() {
        modelMapper = new ModelMapper();
    }


    public <T, U> U map(T source, Class<U> destinationType) {
        return modelMapper.map(source, destinationType);
    }

    public <D, E> List<E> mapDtoListToEntityList(List<D> dtoList, Class<E> entityClass) {
        return dtoList.stream()
                .map(dto -> map(dto, entityClass))
                .collect(Collectors.toList());
    }
    public <E, D> List<D> mapEntityListToDtoList(List<E> entityList, Class<D> dtoClass) {
        return entityList.stream()
                .map(dto -> map(dto, dtoClass))
                .collect(Collectors.toList());
    }
}