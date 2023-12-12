package com.aes.erp.inventory.service;

import com.aes.erp.inventory.entity.SubcategoryBrand;
import com.aes.erp.inventory.repository.SubcategoryBrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class SubcategoryBrandServiceImpl implements SubcategoryBrandService{
    private final SubcategoryBrandRepository subcategoryBrandRepository;

    public SubcategoryBrandServiceImpl(SubcategoryBrandRepository subcategoryBrandRepository) {
        this.subcategoryBrandRepository = subcategoryBrandRepository;
    }

    @Override
    public Page<?> getAllBrands(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return subcategoryBrandRepository.findAll(pageable);
    }

    @Override
    public void create(String name) {
        SubcategoryBrand brand = new SubcategoryBrand();
        brand.setName(name);
        subcategoryBrandRepository.save(brand);
    }

}
