package com.aes.erp.inventory.service;

import com.aes.erp.inventory.entity.CategoryAttribute;

import java.util.List;

public interface CategoryAttributeService {

    List<CategoryAttribute> getAttributesByCategory(Long categoryId);
}
