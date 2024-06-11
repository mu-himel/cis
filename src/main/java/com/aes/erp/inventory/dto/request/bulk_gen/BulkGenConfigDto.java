package com.aes.erp.inventory.dto.request.bulk_gen;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.aes.erp.common.EntityConvertable;
import com.aes.erp.inventory.entity.BulkItemGenConfig;
import com.aes.erp.inventory.entity.ItemCategory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.hash.Hashing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkGenConfigDto implements EntityConvertable<BulkItemGenConfig>{
    
    private BulkGenCategoryDto itemCategory;
    private BulkGenCategoryDto itemParentCategory;
    private List<BulkGenAttributeDto> attributes;
    private List<BulkGenBrandDto> brands;

    @Override
    @JsonIgnore
    public BulkItemGenConfig getEntity() {
        String jsonString = "";

        if(itemCategory==null || itemCategory.getId()==null){
            throw new RuntimeException("Sorry! Sub Category Required");
        }

        if(itemParentCategory==null || itemParentCategory.getId()==null){
            throw new RuntimeException("Sorry! Category Required");
        }

        if(attributes==null || attributes.size()==0){
            throw new RuntimeException("Sorry! Sub Category Attributes Required");
        }
        
        // if(brands==null || brands.size()==0){
        //     throw new RuntimeException("Sorry! Brand Required");
        // }


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            jsonString = objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        
        if(jsonString!=null){
            BulkItemGenConfig bulkItemGenConfig = new BulkItemGenConfig();
            bulkItemGenConfig.setCategory(new ItemCategory(itemCategory.getId()));
            bulkItemGenConfig.setParentCategory(new ItemCategory(itemParentCategory.getId()));
            String hashValue = Hashing.sha256().hashString(jsonString, StandardCharsets.UTF_8).toString();
            bulkItemGenConfig.setConfigHash(hashValue);
            return bulkItemGenConfig;
        }
        return null;
    }


    
}
