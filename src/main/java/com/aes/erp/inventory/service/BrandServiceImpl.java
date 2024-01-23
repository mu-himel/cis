package com.aes.erp.inventory.service;

import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.SubcategoryBrandRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class BrandServiceImpl implements BrandService{
    private final BrandRepository brandRepository;
    
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public Page<?> getAllBrands(Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        return brandRepository.findAll(pageable);
    }

    @Override
    public void create(String name) {
        Brand brand = new Brand();
        brand.setName(name);
        brandRepository.save(brand);
    }

}
