package com.aes.erp.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

interface BrandService {
    Page<?> getAllBrands(Optional<Integer> page, Optional<Integer> size);
    void create(String name);
    List<?> getAllBySubCategoryId(Long id);
}
