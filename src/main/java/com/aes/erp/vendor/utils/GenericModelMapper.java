package com.aes.erp.vendor.utils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class GenericModelMapper {
    private final ModelMapper modelMapper;
    public GenericModelMapper() {
        modelMapper = new ModelMapper();
        // Custom converter Added to map list To Arraylist
        modelMapper.addConverter(new Converter<ArrayList, List>() {
            public List convert(MappingContext<ArrayList, List> context) {
                return context.getSource();
            }
        });
    }
    @Bean
    public ModelMapper modelMapper() {
        return modelMapper;
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