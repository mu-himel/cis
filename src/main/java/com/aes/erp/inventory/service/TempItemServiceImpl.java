package com.aes.erp.inventory.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aes.erp.inventory.dto.request.bulk_gen.SearchAttributeDto;
import com.aes.erp.inventory.dto.request.bulk_gen.SearchInactiveProduct;
import com.aes.erp.inventory.dto.response.TempItemResponse;
import com.aes.erp.inventory.entity.Brand;

@Service
public class TempItemServiceImpl implements TempItemService{

    @Autowired
    private EntityManager entityManager;

    @Override
    public List<TempItemResponse> searchInactiveItems(SearchInactiveProduct searchInactiveProduct) {
        String sql = """
                SELECT ti.id as id, item_attribute_name as itemAttributeName, b.name as brandName,
                ic.name as categoryName, ipc.name as parentCategoryName, ic.code as categoryCode,
                ipc.code as parentCategoryCode, ti.code as productCode 
                FROM temp_items ti
                LEFT JOIN temp_item_attributes tia on tia.item_id = ti.id
                LEFT JOIN brands b ON ti.brand_id = b.id
                LEFT JOIN item_categories as ic ON ic.id = ti.item_category_id
                LEFT JOIN item_categories as ipc ON ipc.id = ti.item_parent_category_id
               
                """;
                List<String> attrTypes = new ArrayList<>();
                List<String> attrValues = new ArrayList<>();
                for(SearchAttributeDto attribute : searchInactiveProduct.getAttributes()){
                    attrTypes.add("\""+attribute.getAttributeType()+"\"");
                    attrValues.addAll(attribute.getAttributeValue().stream().map(av->av+ " "+attribute.getAttributeUnit()).toList());
                }
        if(attrTypes.size()>0){
            sql += " WHERE  (attribute_type IN ("+String.join(",", attrTypes)+"))";
        }
        
        sql += " GROUP BY ti.id"; 
        Query q = entityManager.createNativeQuery(sql, "TempItemRes");
        List<TempItemResponse>  tempItemResponses = q.getResultList();
        List<TempItemResponse> filteredTempItems = new ArrayList<>();
        List<TempItemResponse> brandFilteredTempItems = new ArrayList<>();
        for(TempItemResponse itemResponse : tempItemResponses){
            String itemName = itemResponse.getItemAttributeName();
            int matchCount = 0;
            int size = attrValues.size();
            for(String av : attrValues){
                if(itemName.contains(av)){
                    matchCount++;
                }
            }
            if((matchCount>1 && size>1) || matchCount==size){
                filteredTempItems.add(itemResponse);
            }
        }

        if(searchInactiveProduct.getBrands()!=null && searchInactiveProduct.getBrands().size()>0){
            for(Brand b : searchInactiveProduct.getBrands()){
                for(TempItemResponse fi : filteredTempItems){
                    if(fi.getBrandName().equals(b.getName())){
                        brandFilteredTempItems.add(fi);
                    }
                }
            }
        }
        
        
        return brandFilteredTempItems;


    }
    
}
