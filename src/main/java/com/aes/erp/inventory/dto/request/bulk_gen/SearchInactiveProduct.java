package com.aes.erp.inventory.dto.request.bulk_gen;

import java.util.List;

import com.aes.erp.inventory.entity.Brand;

import lombok.Data;

@Data
public class SearchInactiveProduct {
    List<SearchAttributeDto> attributes;
    List<Brand> brands;
}
