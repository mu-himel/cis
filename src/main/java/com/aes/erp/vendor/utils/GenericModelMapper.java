package com.aes.erp.vendor.utils;
import com.aes.erp.scm.Entities.PriceQuotation;
import com.aes.erp.vendor.dto.OfferItemCreateDto;
import com.aes.erp.vendor.dto.PriceQuotationCreateDto;
import com.aes.erp.vendor.entity.RFQ_Negotiation.OfferItem;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
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
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        modelMapper.addConverter(new Converter<ArrayList, List>() {
            public List convert(MappingContext<ArrayList, List> context) {
                return context.getSource();
            }
        });
//        modelMapper.addConverter(new Converter<Object, Object>() {
//            @Override
//            public Object convert(MappingContext<Object, Object> mappingContext) {
//               if(mappingContext.getSourceType().equals(PriceQuotationCreateDto.class) && mappingContext.getDestinationType().equals(PriceQuotation.class)){
//                   PriceQuotationCreateDto source = (PriceQuotationCreateDto) mappingContext.getSource();
//                   PriceQuotation destination;
//                   destination = modelMapper.map(source, PriceQuotation.class);
//                   return destination;
//               }
//               if(mappingContext.getSourceType().equals(OfferItemCreateDto.class) && mappingContext.getDestinationType().equals(OfferItem.class)){
//                   OfferItemCreateDto source = (OfferItemCreateDto) mappingContext.getSource();
//                   OfferItem destination;
//                   destination = modelMapper.map(source, OfferItem.class);
//                   destination.setPriceQuotation(modelMapper.map(source.getPriceQuotation(), PriceQuotation.class));
//                   return destination;
//               }
//               return null;
//            }
//        });
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