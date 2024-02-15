package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.enums.CategoryHeader;
import com.aes.erp.inventory.enums.SubCategoryHeader;

import com.google.common.collect.Lists;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BulkUploadServiceImpl implements BulkUploadService{

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CategoryService categoryService;

    public void categoryBulkUpload(Optional<StoreType> storeTypeOp,
                                   Optional<MultipartFile> file
    ) throws IOException {
        Path path = Path.of("./uploads/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());

            Iterable<CSVRecord> records = getCategoryRecords(fileUploadResponse);

            for(CSVRecord r:records){
                String catName = r.get("CATEGORY_NAME");
                List<ItemCategory> catOp = categoryService.existCategoryByNameIgnoreCase(catName.trim());
                if(catOp.size()==0){
                    String code = categoryService.getNewCategoryCode();
                    CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
                    categoryRequestDto.setName(catName);
                    categoryRequestDto.setCode(code);
                    categoryRequestDto.setStoreType(storeTypeOp.get());
                    categoryService.addCategory(categoryRequestDto);
                }
            }
        }
    }


    @Override
    @Transactional
    public void subCategoryBulkUpload( Optional<MultipartFile> file) throws IOException {

//        Optional<ItemCategory> catOp = categoryService.getItemCategory(categoryId);
//        if(catOp.isEmpty()){
//            throw new AesException("Category not found");
//        }

        Path path = Path.of("./uploads/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());
            Iterable<CSVRecord> records = getSubCategoryRecords(fileUploadResponse);
            Map<String,Object> subCat = new HashMap<>();
            for(CSVRecord r:records){
                // String storeType = r.get("STORE_TYPE");
                String catName = r.get("CATEGORY_NAME");
                String subCatName = r.get("SUB_CATEGORY_NAME");
                // String itemName = r.get("ITEM_NAME");
                String vat = r.get("VAT");
                String attrType = r.get("ATTRIBUTE_TYPE");
                String attrValue = r.get("ATTRIBUTE_VALUE");
                String attrUnit = r.get("ATTRIBUTE_UNIT");
                String brands = r.get("BRANDS");
                String _key =subCatName.replaceAll(" ","_").toLowerCase();

                if(!subCat.containsKey(_key)) {
                    Map<String, Object> subCatProps = new HashMap<>();
                    subCatProps.put("name", subCatName);
                    subCatProps.put("catName",catName);
                    subCatProps.put("vat",vat);
                    subCatProps.put("brands", Arrays.asList(brands.split(",")));
                    List<Map<String,Object>> attrs = new ArrayList<>();
                    Map<String ,Object> attr =  new HashMap<>();
                    attr.put("attributeType",attrType);
                    attr.put("attributeValue",attrValue);
                    attr.put("attributeUnit",attrUnit);
                    attrs.add(attr);
                    subCatProps.put("attributes",attrs);
                    subCat.put(_key, subCatProps);
                }else{
                    Map<String, Object> subCatProps = (Map<String, Object>) subCat.get(_key);
                    List<Map<String,Object>> attrs = (List<Map<String,Object>>)subCatProps.get("attributes");
                    Map<String ,Object> attr =  new HashMap<>();
                    attr.put("attributeType",attrType);
                    attr.put("attributeValue",attrValue);
                    attr.put("attributeUnit",attrUnit);
                    attrs.add(attr);

                }
            }
            subCat.values().stream().forEach(_subCat->{

                String catName = ((Map<String, Object>)_subCat).get("catName").toString();
                String subCatName = ((Map<String, Object>)_subCat).get("name").toString();
                String vat = ((Map<String, Object>)_subCat).get("vat").toString();

                List<String> brands = (List<String>) ((Map<String, Object>)_subCat).get("brands");
                List<Map<String,Object>> attributes = (List<Map<String,Object>>)((Map<String, Object>)_subCat).get("attributes");

                List<ItemCategory> subCatOp = categoryService.existCategoryBySubCatNameIgnoreCase(subCatName.trim());
                if(subCatOp.size()==0) {
                    List<ItemCategory> catOp = categoryService.existCategoryByNameIgnoreCase(catName.trim());
                    ItemCategory category = catOp.stream().findFirst().get();

                    String code = categoryService.getNewCategoryCode();
                    CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
                    categoryRequestDto.setName(subCatName);
                    categoryRequestDto.setCode(code);
                    String vatPercentage =vat.replace("%","");
                    if(!vatPercentage.isEmpty()){
                        categoryRequestDto.setVat(BigDecimal.valueOf(Long.valueOf(vatPercentage)));
                    }
                    categoryRequestDto.setParentCategory(category);
                    categoryRequestDto.setStoreType(category.getStoreType());
                    categoryRequestDto.setBrands(brands);
                    categoryRequestDto.setAttributes(
                            attributes.stream().map(attr->{
                                CategoryAttribute ca = new CategoryAttribute();
                                ca.setAttributeType((String)attr.get("attributeType"));
                                ca.setAttributeValue((String)attr.get("attributeValue"));
                                ca.setAttributeUnit((String)attr.get("attributeUnit"));
                                return ca;
                            }).collect(Collectors.toList())
                    );
                    categoryService.addCategory(categoryRequestDto);
                }
            });

//            System.out.println();

        }
    }

    private Iterable<CSVRecord> getCategoryRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath()+"/"+fileUploadResponse.getFilename());
        Iterable<CSVRecord> records  = CSVFormat.RFC4180.withHeader(CategoryHeader.class).parse(in);
        records.iterator().next();
        return records;
    }
    
    private Iterable<CSVRecord> getSubCategoryRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath()+"/"+fileUploadResponse.getFilename());
        Iterable<CSVRecord> records  = CSVFormat.RFC4180.withHeader(SubCategoryHeader.class).parse(in);
        records.iterator().next();
        return records;
    }
}
