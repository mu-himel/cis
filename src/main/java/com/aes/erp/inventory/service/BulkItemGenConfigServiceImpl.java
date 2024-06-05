package com.aes.erp.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aes.erp.inventory.dto.request.bulk_gen.BulkGenConfigDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.BulkGenAttribute;
import com.aes.erp.inventory.entity.BulkGenBrand;
import com.aes.erp.inventory.entity.BulkItemGenConfig;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.repository.BulkItemGenConfigRepository;

@Service
public class BulkItemGenConfigServiceImpl implements BulkItemGenConfigService{

    @Autowired
    private BulkItemGenConfigRepository bulkItemGenConfigRepository;

    @Autowired
    private BulkItemGenerationProcessService bulkItemGenerationProcessService;

    @Autowired
    private CategoryService categoryService;

    @Override
    @Transactional
    public Long saveConfig(BulkGenConfigDto bulkGenConfigDto) {
        
        BulkItemGenConfig bulkItemGenConfig = bulkGenConfigDto.getEntity();
        Optional<BulkItemGenConfig> configOp = bulkItemGenConfigRepository.findByConfigHash(bulkItemGenConfig.getConfigHash());
        if(configOp.isEmpty()){
            Optional<ItemCategory> seleectedCategoryOp = categoryService.getItemCategory(bulkGenConfigDto.getItemCategory().getId());
            if(seleectedCategoryOp.isEmpty()){
                throw new RuntimeException("Sorry! Category not found");
            }
            ItemCategory seleectedCategory = seleectedCategoryOp.get();
            List<CategoryAttribute> categoryAttributes = new ArrayList<>();
            bulkItemGenConfig.setAttributes(bulkGenConfigDto.getAttributes().stream().map(attr->{
                BulkGenAttribute bulkGenAttribute = new BulkGenAttribute();
                CategoryAttribute categoryAttribute = new CategoryAttribute();

                bulkGenAttribute.setAttributeType(attr.getAttributeType());
                bulkGenAttribute.setAttributeUnit(attr.getAttributeUnit());
                bulkGenAttribute.setAttributeValue(attr.getAttributeValue());
                bulkGenAttribute.setBulkItemGenConfig(bulkItemGenConfig);

                categoryAttribute.setAttributeType(attr.getAttributeType());
                categoryAttribute.setAttributeUnit(attr.getAttributeUnit());
                categoryAttribute.setAttributeValue(attr.getAttributeValue());
                categoryAttributes.add(categoryAttribute);
                return bulkGenAttribute;
            }).collect(Collectors.toList()));
            seleectedCategory.setAttributes(categoryAttributes);
            bulkItemGenConfig.setBrands(bulkGenConfigDto.getBrands().stream().map(b->{
                BulkGenBrand bulkGenBrand = new BulkGenBrand();
                Brand brand = new Brand(b.getId(), b.getName());
                bulkGenBrand.setBrand(brand);
                bulkGenBrand.setBulkItemGenConfig(bulkItemGenConfig);
                return bulkGenBrand;
            }).collect(Collectors.toList()));
            bulkItemGenConfigRepository.save(bulkItemGenConfig);

            List<ItemCategory> categories = new ArrayList<>();
            
            categories.add(seleectedCategory);
            
            bulkItemGenerationProcessService.getPermuttedItems(bulkItemGenConfig,categories);

            return bulkItemGenConfig.getId();
        }
        
        return configOp.get().getId();
        
    }
    
}
