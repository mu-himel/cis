package com.aes.erp.inventory.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.dto.request.CategoryAttributeDto;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.dto.request.import_item.ItemImportReq;
import com.aes.erp.inventory.entity.*;
import com.aes.erp.inventory.enums.CategoryHeader;
import com.aes.erp.inventory.enums.ProductHeader;
import com.aes.erp.inventory.enums.SubCategoryHeader;

import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.CategoryRepository;
import com.google.common.hash.Hashing;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BulkUploadServiceImpl implements BulkUploadService {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private ItemService itemService;

    @Value("${uploadDir}")
    private String uploadDir;

    public void categoryBulkUpload(
            // Optional<StoreType> storeTypeOp,
            Optional<MultipartFile> file
    ) throws IOException {
        Path path = Path.of(uploadDir + "/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());

            Iterable<CSVRecord> records = getCategoryRecords(fileUploadResponse);

            for(CSVRecord r:records){
                String catName = r.get("CATEGORY_NAME").trim();
                String prefix= r.get("PREFIX");
//                List<ItemCategory> catOp = categoryService.existCategoryByNameIgnoreCase(catName.trim());
//                if(catOp.size()==0){

                String code = categoryService.getNewCategoryCode(catName.substring(0, 1), prefix, Optional.empty());
                StringBuilder generated_code = new StringBuilder();
                generated_code.append(prefix);
                generated_code.append(catName.substring(0, 1));
                generated_code.append(code);
                CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
                categoryRequestDto.setName(catName);
                categoryRequestDto.setPrefix(prefix);
//                categoryRequestDto.setCode(generated_code.toString());
                // categoryRequestDto.setStoreType(storeTypeOp.get());
                categoryService.addCategory(categoryRequestDto);
//                }
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

        Path path = Path.of(uploadDir+"/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());
            Iterable<CSVRecord> records = getSubCategoryRecords(fileUploadResponse);
            Map<String,Object> subCat = new HashMap<>();
            for(CSVRecord r:records){
                // String storeType = r.get("STORE_TYPE");
                String catName = r.get("CATEGORY_NAME").trim();
                String subCatName = r.get("SUB_CATEGORY_NAME").trim();
                // String itemName = r.get("ITEM_NAME");
//                String vat = r.get("VAT");
                String attrType = r.get("ATTRIBUTE_TYPE").trim();
                String attrValue = r.get("ATTRIBUTE_VALUE").trim();
                String attrUnit = r.get("ATTRIBUTE_UNIT").trim();
                String brands = r.get("BRANDS");
                String _key =subCatName.replaceAll(" ","_").toLowerCase();

                if(!subCat.containsKey(_key)) {
                    Map<String, Object> subCatProps = new HashMap<>();
                    subCatProps.put("name", subCatName);
                    subCatProps.put("catName",catName);
//                    subCatProps.put("vat",vat);
                    subCatProps.put("brands", Arrays.asList(brands.split(",")).stream().map(b->b.trim()).toList());
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

                String catName = ((Map<String, Object>) _subCat).get("catName").toString();
                String subCatName = ((Map<String, Object>) _subCat).get("name").toString();
//                String vat = ((Map<String, Object>)_subCat).get("vat").toString();

                List<String> brands = (List<String>) ((Map<String, Object>) _subCat).get("brands");
                List<Map<String, Object>> attributes = (List<Map<String, Object>>) ((Map<String, Object>) _subCat).get("attributes");
                Optional<ItemCategory> itemCategoryOptional = categoryService.getByName(catName.trim());
                if (itemCategoryOptional.isEmpty()) {
                    throw new AesException("Sorry No Parent Category Found");
                }
                ItemCategory itemCategory = itemCategoryOptional.get();
                List<ItemCategory> subCatOp = categoryService.existCategoryByParentCategoryIdSubCatNameIgnoreCase(itemCategory.getId(), subCatName.trim());
                if (subCatOp.size() == 0) {
//                    List<ItemCategory> catOp = categoryService.existCategoryByNameIgnoreCase(catName.trim());
//                    ItemCategory category = catOp.stream().findFirst().orElse(null);

                    if (itemCategory != null) {
                        String code = categoryService.getNewCategoryCode(subCatName.substring(0, 1), Optional.ofNullable(itemCategory.getId()));
                        StringBuilder sb = new StringBuilder();
                        sb.append(itemCategory.getCode());
                        sb.append("-");
                        sb.append(subCatName.substring(0, 1));
                        sb.append(code);
                        CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
                        categoryRequestDto.setName(subCatName);
                        categoryRequestDto.setPrefix(itemCategory.getCode());
//                        categoryRequestDto.setCode(sb.toString());
//                        String vatPercentage =vat.replace("%","");
//                        if(!vatPercentage.isEmpty()){
//                            categoryRequestDto.setVat(BigDecimal.valueOf(Long.valueOf(vatPercentage)));
//                        }
                        categoryRequestDto.setParentCategory(itemCategory);
                        // categoryRequestDto.setStoreType(category.getStoreType());
                        categoryRequestDto.setBrands(brands);
                        categoryRequestDto.setAttributes(
                                attributes.stream().map(attr -> {
                                    CategoryAttribute ca = new CategoryAttribute();
                                    ca.setAttributeType((String)attr.get("attributeType"));
                                    ca.setAttributeValue((String)attr.get("attributeValue"));
                                    ca.setAttributeUnit((String)attr.get("attributeUnit"));
                                    return ca;
                                }).collect(Collectors.toList())
                        );
                        categoryService.addCategory(categoryRequestDto);
                    }
                    
                }
            });

//            System.out.println();

        }
    }

    private Iterable<CSVRecord> getCategoryRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath() + "/" + fileUploadResponse.getFilename());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(CategoryHeader.class).parse(in);
        records.iterator().next();
        return records;
    }

    @Override
    @Transactional
    public void productUpload(ClaimResponseDto claimResponseDto, Optional<MultipartFile> file) throws IOException {
        Path path = Path.of(uploadDir + "/inventory-control");

        List<ItemImportReq> products = new ArrayList<>();
        if (file.isPresent()) {
            FileUploadResponse fileUploadResponse = fileUploadService.uploadFile(path, file.get());
            Iterable<CSVRecord> records = getProductRecords(fileUploadResponse);
            Long i = 0L, ri = 2L;
            Map<String, Object> data = new HashMap<>();
            for (CSVRecord r : records) {
                String prefix = r.get("PREFIX").trim();
                String catName = r.get("CATEGORY_NAME").trim();
                String subCatName = r.get("SUB_CATEGORY_NAME").trim();
                String attrTypes = r.get("ATTRIBUTE_TYPE").trim();
                String attrValue = r.get("ATTRIBUTE_VALUE").trim();
                String attrUnit = r.get("ATTRIBUTE_UNIT").trim();
                String brands = r.get("BRANDS");
                String purchaseUnit = r.get("PURCHASE_UNIT");

                List<String> brandsArr = Arrays.asList(brands.split(","));
                List<String> attrVals = Arrays.asList(attrValue.split(","));
                List<String> attrTypesArr = Arrays.asList(attrTypes.split(","));


                //StringBuilder sb = new StringBuilder();


                for (String brand : brandsArr) {
                    ItemImportReq itemImportReq = null;
                    String dataKey = catName.trim() + "_" + subCatName.trim() + "_" + brand.trim();
                    if (!data.containsKey(dataKey)) {
                        itemImportReq = new ItemImportReq();
                        itemImportReq.setCsvIndex(ri);
                        itemImportReq.setBrandName(brand);
                        itemImportReq.setCategoryName(catName);
                        itemImportReq.setSubCategoryName(subCatName);
                        itemImportReq.setUnit(purchaseUnit);
                        itemImportReq.setAttributeDtoList(new ArrayList<>());
                        data.put(dataKey, itemImportReq);
                    } else {
                        itemImportReq = (ItemImportReq) data.get(dataKey);
                    }

                    //sb.append(catName).append(",").append(subCatName).append(",").append(brand)
                    //       .append(",").append(purchaseUnit).append(",");
//                    data.put("category",catName);
//                    data.put("subCategory",subCatName);
//                    data.put("brand", brand);

                    List<CategoryAttributeDto> attributeDtos = itemImportReq.getAttributeDtoList();
                    for (String attrType : attrTypesArr) {
                        Optional<CategoryAttributeDto> catAttrDtoOp = attributeDtos.stream().filter(attr -> attr.getAttributeType().trim().equals(attrType.trim())).findFirst();
                        if (catAttrDtoOp.isEmpty()) {
                            CategoryAttributeDto catAttrDto = new CategoryAttributeDto();
                            for (String attrVal : attrVals) {
                                //sb.append(attrType).append(",").append(attrVal).append(",").append(attrUnit);
                                catAttrDto.setAttributeType(attrType);
                                catAttrDto.setAttributeValue(attrVal);
                                catAttrDto.setAttributeUnit(attrUnit);
                                i++;
                                System.out.print("\rItem Count:" + i);
                            }
                            if (catAttrDto.getAttributeValue().trim().length() > 0) {
                                attributeDtos.add(catAttrDto);
                            }
                        }
                    }
                    itemImportReq.setAttributeDtoList(attributeDtos);
//                    products.add(itemImportReq);
                }
                ri++;
            }

            Map<String, Object> catExist = new HashMap<>();
            Map<String, Object> brandExist = new HashMap<>();
            Long index = 0L;
            System.out.println();
            System.out.println("Total CSV Index: " + ri + "\n");
            products = data.entrySet().stream().map(e -> {
                return (ItemImportReq) e.getValue();
            }).collect(Collectors.toList());
            for (ItemImportReq product : products) {
                System.out.print("\r Csv Index: " + product.getCsvIndex() + " ");
                String _key = product.getCategoryName() + product.getSubCategoryName();
                if (!catExist.containsKey(_key)) {
                    Optional<CategoryRepository.CatSubCatInfo> catSubCatOp = categoryService
                            .getCatSubCatId(
                                    product.getCategoryName(),
                                    product.getSubCategoryName()
                            );
                    if (catSubCatOp.isPresent()) {
                        CategoryRepository.CatSubCatInfo catSubCatInfo = catSubCatOp.get();
                        catExist.put(_key, catSubCatInfo);
                    }
                }
                String brandName = product.getBrandName().trim();
                if (!brandExist.containsKey(brandName)) {
                    Optional<Brand> brandOp = brandRepository.findByName(brandName);
                    if (brandOp.isPresent()) {
                        Brand brand = brandOp.get();
                        brandExist.put(brandName, brand);
                    } else {
                        Brand brand = new Brand();
                        brand.setName(brandName);
                        brand = brandRepository.save(brand);
                        brandExist.put(brandName, brand);
                    }
                }
                CategoryRepository.CatSubCatInfo catSubCatInfo = (CategoryRepository.CatSubCatInfo) catExist.get(_key);
                Brand brand = (Brand) brandExist.get(brandName);
                ItemRequestDto itemRequestDto = new ItemRequestDto();
                itemRequestDto.setItemUnit(product.getUnit());
                if (catSubCatInfo == null) {
                    System.out.println("DEBUG  : [" + product.getCategoryName() + "] [" + product.getSubCategoryName() + "]");
                } else {
                    itemRequestDto.setCode(catSubCatInfo.getSubCatCode());
                    itemRequestDto.setItemCategory(new ItemCategory(catSubCatInfo.getSubCatId()));
                    itemRequestDto.setItemParentCategory(new ItemCategory(catSubCatInfo.getCatId()));
                    itemRequestDto.setName(product.getBrandName());
                    if (Objects.nonNull(brand)) {
                        itemRequestDto.setBrand(new ReferenceObjectDto(brand.getId()));
                    }
                    itemRequestDto.setAttributes(product.getAttributeDtoList().stream()
                            .map(attr -> {
                                ItemAttribute itemAttribute = new ItemAttribute();
                                itemAttribute.setAttributeType(attr.getAttributeType());
                                itemAttribute.setAttributeValue(attr.getAttributeValue());
                                itemAttribute.setAttributeUnit(attr.getAttributeUnit());
                                return itemAttribute;
                            }).collect(Collectors.toList()));
                    itemService.importItem(claimResponseDto, itemRequestDto);

                }
                index++;
            }
            System.out.println("import finished");
        }
//        return Optional.ofNullable(products);
    }

    private Iterable<CSVRecord> getSubCategoryRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath() + "/" + fileUploadResponse.getFilename());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(SubCategoryHeader.class).parse(in);
        records.iterator().next();
        return records;
    }

    private Iterable<CSVRecord> getProductRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath() + "/" + fileUploadResponse.getFilename());
        Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader(ProductHeader.class).parse(in);
        records.iterator().next();
        return records;
    }
}
