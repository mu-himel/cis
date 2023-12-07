package com.aes.erp.vendor.utils;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class GenericMapper {
    @Bean
    public ModelMapper modelMapper() {
        return modelMapper;
    }
    private final ModelMapper modelMapper;
    public GenericMapper() {
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
}