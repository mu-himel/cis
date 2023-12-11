package com.aes.erp.inventory.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface SubcategoryBrandService {
    Page<?> getAllBrands(Optional<Integer> page, Optional<Integer> size);
}
