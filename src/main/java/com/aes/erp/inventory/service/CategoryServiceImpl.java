package com.aes.erp.inventory.service;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.BulkCategoryRequestDto;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.dto.request.ImportCategoryScmIdUpdateDto;
import com.aes.erp.inventory.dto.request.MergePendingCategoryDto;
import com.aes.erp.inventory.dto.request.MergePendingCategoryPostDto;
import com.aes.erp.inventory.dto.response.SubCategory;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.SubCategoryBrand;
import com.aes.erp.inventory.enums.CategoryStatus;
import com.aes.erp.inventory.repository.*;
import com.aes.erp.network.NetworkService;
import com.aes.erp.vendor.utils.GenericModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private static final Integer PAGE_SIZE = 10;
    @Autowired
    private CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    @Autowired
    private StoreTypeRepository storeTypeRepository;

    @Autowired
    private CategoryBudgetRepository categoryBudgetRepository;

    private final GenericModelMapper genericModelMapper;
    private final SubcategoryBrandRepository subcategoryBrandRepository;
    @Autowired
    private CategoryAttributeRepository categoryAttributeRepository;

    @Autowired
    private NetworkService networkService;

    @Value("${scm.apiEndpoint}")
    private String scmApiEndpoint;

    public CategoryServiceImpl(BrandRepository brandRepository, GenericModelMapper genericModelMapper, SubcategoryBrandRepository subcategoryBrandRepository) {
        this.brandRepository = brandRepository;
        this.genericModelMapper = genericModelMapper;
        this.subcategoryBrandRepository = subcategoryBrandRepository;
    }

    public void addBrandToSubCategory(CategoryRequestDto dto, ItemCategory category){
        if(dto.getBrands() != null){
            Set<SubCategoryBrand> subCategoryBrands = new HashSet<>();
            for(String brandName: dto.getBrands()) {
                Optional<SubCategoryBrand> existingBrandOptional = subcategoryBrandRepository.getBrandByNameAndSubCategoryId(brandName, category.getId());
                if(existingBrandOptional.isEmpty()){
                    SubCategoryBrand subcategoryBrand = new SubCategoryBrand();
                    Optional<Brand> brandOptional = brandRepository.findByName(brandName);
                    Brand brandToBeAdded = new Brand();
                    if(brandOptional.isEmpty()){
                        brandToBeAdded.setName(brandName);
                        brandToBeAdded = brandRepository.save(brandToBeAdded);
                    }
                    else brandToBeAdded = brandOptional.get();
                    subcategoryBrand.setBrand(brandToBeAdded);
                    subcategoryBrand.setSubcategory(category);
                    subcategoryBrand = subcategoryBrandRepository.save(subcategoryBrand);
                    subCategoryBrands.add(subcategoryBrand);
                }
            }
            category.setSubcategoryBrands(subCategoryBrands);
        }
        //If Its a subcategory Brands Must be included.
        if(category.getParentCategory() != null && dto.getBrands() == null) throw new AesException("Brands must included to create a subcategory");
    }
    @Override
    @Transactional
    public Map<String,Object> addCategory(CategoryRequestDto categoryRequestDto) {
        Map<String, Object> returnMap = new HashMap<>();
        ItemCategory category = categoryRequestDto.getEntity();
        Optional<Long> _parentCatIdOp = (categoryRequestDto.getParentCategory() != null) ?
                Optional.ofNullable(categoryRequestDto.getParentCategory().getId()) : Optional.empty();
        category.setCode(getNewCategoryCode(category.getName().substring(0, 1), categoryRequestDto.getPrefix(), _parentCatIdOp));
        category.setName(category.getName().trim());
        if (categoryRequestDto.getOrganization() != null) {
            category.setOrganization(categoryRequestDto.getOrganization());
        }
        Optional<ItemCategory> itemCategoryOpt = categoryRepository.getByNameAndActiveAndCode(category.getName().toUpperCase(), false, category.getCode());
//        Optional<ItemCategory> itemCategoryOpt = categoryRepository.findByNameAndActive(category.getName().toUpperCase(),false);
//        Optional<ItemCategory> itemCategoryOpt = categoryRepository.findByCodeAndActive(category.getCode().toUpperCase(),false);
        if (itemCategoryOpt.isPresent()) {
//            throw new AesException("Category code already exist");
            ItemCategory itemCategory = itemCategoryOpt.get();
            System.out.println("Category name already exist and deactivated");
            if(category.getParentCategory() == null) {
                //category
                //check if this product prefix matches with given prefix
                if (itemCategory.getCode().substring(0, 1).equals(category.getCode().substring(0, 1))) {
                    itemCategory.setActive(true);
                    categoryRepository.save(itemCategory);
                    returnMap.put("id", itemCategory.getId());
                    returnMap.put("code", itemCategory.getCode());
                    returnMap.put("message", "Category with name " + category.getName() + " was created before with code " + itemCategory.getCode() + ". Replace " + category.getCode() + "by previously assigned code" + itemCategory.getCode());
                    return returnMap;
                }
            }else {
                //
                if (itemCategory.getCode().substring(0, 4).equals(category.getCode().substring(0, 4))) {
                    if(category.getParentCategory().getId().equals(itemCategory.getParentCategory().getId())){
                        itemCategory.setActive(true);
                        addAtrToSubcategory(categoryRequestDto, itemCategory);
                        addBrandToSubCategory(categoryRequestDto,itemCategory);
                        categoryRepository.save(itemCategory);
                        returnMap.put("id", itemCategory.getId());
                        returnMap.put("code", itemCategory.getCode());
                        returnMap.put("message", "SubCategory with name " + category.getName() + " was created before with code " + itemCategory.getCode() + ". Replace " + category.getCode() + "by previously assigned code" + itemCategory.getCode());
                        return returnMap;
                    }
                }
            }
        }
        if (categoryRepository.existsByCodeAndActive(category.getCode(),true)) {
            System.out.println("Category code already exist");
            returnMap.put("id", category.getId());
            returnMap.put("code", category.getCode());
            returnMap.put("message", "Category code " + category.getCode() + " already exist");
            return returnMap;
//            return category.getId();
        }

        //uncomment this to fix bug SDOERP-1223
        if(categoryRequestDto.getParentCategory() == null){
            List<Long> catList = categoryRepository.findDuplicateCategoryId(category.getName(),category.getCode());
            if(!catList.isEmpty()){
                throw new AesException("Sorry! Category Name "+ category.getName()+" already exist with code "+category.getCode());
            }
        }
        else{
            List<Long> subcatList = categoryRepository.findDuplicateSubCategoryId(category.getName(),category.getCode());
            if(!subcatList.isEmpty()){
                throw new AesException("Sorry! Sub Category Name "+ category.getName()+" already exist with code "+category.getCode());
            }
        }

        if(categoryRequestDto.getParentCategory() != null){
            Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(categoryRequestDto.getParentCategory().getId());
            if(itemCategoryOptional.isPresent()) category.setParentCategory(itemCategoryOptional.get());
        }
        
        // if(categoryRequestDto.getStoreType()!=null && categoryRequestDto.getStoreType().getId() != null){
        //     Optional<StoreType> storeType = storeTypeRepository.findById(categoryRequestDto.getStoreType().getId());
        //     if(storeType.isPresent())category.setStoreType(storeType.get());
        // }

        if(categoryRequestDto.getRequestedBy()!=null){
            category.setRequesterName(categoryRequestDto.getRequestedBy());
        }

        if(categoryRequestDto.getOrganization()!=null){
            category.setCategoryStatus(CategoryStatus.PENDING);
            category.setActive(true);
        }
        if(categoryRequestDto.getOrganization()==null) {
//            category.setCategoryStatus(categoryRequestDto.getCategoryStatus());
            category.setCategoryStatus(CategoryStatus.APPROVED);
            category.setActive(true);
        }
        addAtrToSubcategory(categoryRequestDto, category);
        if(categoryRequestDto.getScmCategoryId()!=null){
            category.setScmCategoryId(categoryRequestDto.getScmCategoryId());
        }


        category.setCreatedAt(Instant.now().toEpochMilli());
        categoryRepository.save(category);
        addBrandToSubCategory(categoryRequestDto, category);
        returnMap.put("id", category.getId());
        returnMap.put("code", category.getCode());
        returnMap.put("message", "Successfully Created");
        return returnMap;
//        return category.getId();
    }

    private static void addAtrToSubcategory(CategoryRequestDto categoryRequestDto, ItemCategory category) {
        if(categoryRequestDto.getAttributes()!=null && categoryRequestDto.getAttributes().size()>0){
            ItemCategory finalCategory = category;
            category.setAttributes(categoryRequestDto.getAttributes().stream().map(categoryAttribute -> {
                categoryAttribute.setCategory(finalCategory);
                categoryAttribute.setAttributeType(categoryAttribute.getAttributeType().trim());
                categoryAttribute.setAttributeValue(categoryAttribute.getAttributeValue().trim());
                categoryAttribute.setAttributeUnit(categoryAttribute.getAttributeUnit().trim());
                return categoryAttribute;
            }).collect(Collectors.toList()));
        }
    }

    @Override
    public ItemCategory addCategoryFromCategoryEntity(ItemCategory itemCategory) {
        return categoryRepository.save(itemCategory);
    }

    @Override
    @Transactional
    public void updateCategory(Long id, CategoryRequestDto categoryRequestDto) {
        Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(id);
        if(itemCategoryOptional.isEmpty()){
            throw new AesException("Category Not Found");
        }

        ItemCategory itemCategory = itemCategoryOptional.get();
        if(!itemCategory.getCode().equalsIgnoreCase(categoryRequestDto.getCode())){
            throw new AesException("Category Code should be unique");
        }
        Optional<ItemCategory> itemCatOp = categoryRepository.getByNameAndActiveAndCode(categoryRequestDto.getName().toUpperCase(),true,categoryRequestDto.getCode());

//        Optional<ItemCategory> itemCatOp = categoryRepository.findByNameAndActive(categoryRequestDto.getName(),true);
        if(itemCatOp.isPresent()){
            if(!itemCatOp.get().getId().equals(id)){
                throw new AesException("Sorry! Category exist with this name with #ID:"+itemCatOp.get().getId());
            }
        }

        if(itemCategory.getParentCategory()!=null){
            if(categoryRequestDto.getParentCategory()==null || categoryRequestDto.getParentCategory().getId()==null){
                throw new AesException("Parent Category Id missing");
            }
        }

        if(categoryRequestDto.getName()!=null) {
            itemCategory.setName(categoryRequestDto.getName());
        }

        // if(categoryRequestDto.getStoreType()!=null && categoryRequestDto.getStoreType().getId() != null){
        //     Optional<StoreType> storeType = storeTypeRepository.findById(categoryRequestDto.getStoreType().getId());
        //     if(storeType.isPresent())itemCategory.setStoreType(storeType.get());
        // }

        if(categoryRequestDto.getAttributes()!=null && categoryRequestDto.getAttributes().size()>0){
            ItemCategory finalItemCategory = itemCategory;
            itemCategory.setAttributes(categoryRequestDto.getAttributes().stream().map(categoryAttribute -> {
                categoryAttribute.setCategory(finalItemCategory);
                categoryAttribute.setAttributeType(categoryAttribute.getAttributeType().trim());
                categoryAttribute.setAttributeValue(categoryAttribute.getAttributeValue().trim());
                categoryAttribute.setAttributeUnit(categoryAttribute.getAttributeUnit().trim());
                return categoryAttribute;
            }).collect(Collectors.toList()));
        }
        if(categoryRequestDto.getEntity().getParentCategory()!=null) {
            itemCategory.setParentCategory(categoryRequestDto.getEntity().getParentCategory());
        }
        if(categoryRequestDto.getVat()!=null){
            itemCategory.setVat(categoryRequestDto.getVat());
        }
        categoryRepository.save(itemCategory);
        removeBrandsForSubCategory(itemCategory, categoryRequestDto.getBrands());
        addBrandToSubCategory(categoryRequestDto, itemCategory);
    }
    public void removeBrandsForSubCategory(ItemCategory itemCategory, List<String> brandNames){
        if(brandNames != null){
            List<SubCategoryBrand> subCategoryBrandsToBeDeleted = new ArrayList<>();
            for (SubCategoryBrand subCategoryBrand : itemCategory.getSubcategoryBrands()) {
                if(!brandNames.contains(subCategoryBrand.getBrand().getName())){
                    subCategoryBrandsToBeDeleted.add(subCategoryBrand);
                }
            }
            for(SubCategoryBrand subCategoryBrand: subCategoryBrandsToBeDeleted){
                subcategoryBrandRepository.delete(subCategoryBrand);
                itemCategory.getSubcategoryBrands().remove(subCategoryBrand);
            }
        }
    }

    @Override
    public Optional<ItemCategory> existByCode(String code) {
        return categoryRepository.findByCode(code);
    }

    @Override
    public Optional<ItemCategory> getItemCategory(Long id) {
        Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(id);
        return itemCategoryOptional;
    }

    


    @Override
    public Page<?> getSubCategoriesFilteredByParentCategory(Optional<Integer> page, Optional<Integer> size,
            Optional<Long> parentCategoryId, Optional<String> name, Optional<String> code) {

                Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));

        return categoryRepository.findAllBySubCategoryFilteredByParentCategory(
                parentCategoryId.orElse(null),
                name.orElse(null),code.orElse(null),
                pageable);
    }

    @Override
    public List<?> getSubCategoryListFilteredByParentCategory(Optional<Long> parentCategoryId, Optional<String> name,
            Optional<String> code) {
                return categoryRepository.findAllBySubCategoryFilteredByParentCategory(parentCategoryId.orElse(null),
                name.orElse(null), code.orElse(null));
    }

    @Override
    public Page<?> getSubCategoriesFilteredByStoreTypeAndParentCategory(Optional<Integer> page, Optional<Integer> size,
                                                                    Optional<Long> storeTypeId,
                                                                    Optional<Long> parentCategoryId,
                                                                    Optional<String> name,
                                                                    Optional<String> code) {
//        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10));
        return categoryRepository.findAllBySubCategoryFilteredByStoreTypeAndParentCategory(
                storeTypeId.orElse(null),
                parentCategoryId.orElse(null),
                name.orElse(null),code.orElse(null),
                pageable);
    }

    @Override
    public List<?> getSubCategoryListFilteredByStoreTypeAndParentCategory(Optional<Long> storeTypeId,
                                                                          Optional<Long> parentCategoryId,
                                                                          Optional<String> name,
                                                                          Optional<String> code) {
        return categoryRepository.findAllBySubCategoryFilteredByStoreTypeAndParentCategory(storeTypeId.orElse(null),
                parentCategoryId.orElse(null),
                name.orElse(null), code.orElse(null));
    }

    @Override
    public Page<?> getItemCategoriesForStoreType(Optional<Integer> page, Optional<Integer> size,
                                        Optional<Long> store_type_id, Optional<String> name,
                                                 Optional<String> code) {
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10),sort);

            return categoryRepository.findAllByItemCategoryWithSubCategoryCount(
                    store_type_id.orElse(null),
                    name.orElse(null), code.orElse(null),
                    pageable);
    }

    @Override
    public List<?> getItemCategoryListForStoreType(Optional<Long> id, Optional<String> name, Optional<String> code) {
        return categoryRepository.findAllByItemCategoryWithSubCategoryCount(id.orElse(null),
                name.orElse(null),code.orElse(null));
    }

    

    @Override
    public List<?> getSubCategoryListFilteredByParentCategoryNameOrCode(Optional<Long> categoryId, Optional<String> name, Optional<String> code) {
        return categoryRepository.findAllSubCategory(categoryId.orElse(null),
                name.orElse(null),code.orElse(null));
    }

    @Override
    public Page<?> getItemCategories( Optional<Integer> page, Optional<Integer> size,
                                      Optional<String> name, Optional<String> code,
                                      Optional<BigDecimal> currentYearBudget, Optional<Long> productCount,
                                      Optional<Long> categoryId
                                      ) {



        Integer year  = LocalDate.now().getYear();
        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        
        Page<?> result = null;
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10),sort);


        result = categoryRepository.findAllSubCategories(
                            name.orElse(null),
                            code.orElse(null),
                            currentYearBudget.orElse(null),
                            productCount.orElse(null),
                            categoryId.orElse(null),
                            year,pageable);

        return result;
    }

    @Override
    public List<?> getCategories(Optional<String> name, Optional<String> code) {

        return categoryRepository.findAllMainCategories(name.orElse(null),code.orElse(null));
    }

    @Override
    public List<?> getSubCategoriesByParentIdAndSearchFilter(Optional<Long> categoryId, Optional<String> categoryName) {
        return categoryRepository.findAllSubCategories(categoryId.orElse(null), categoryName.orElse(""));
    }

    @Override
    public List<?> getSubCategories(Optional<Long> id, Optional<String> name, Optional<String> code) {

        return categoryRepository.findAllSubCategories(
                id.orElse(null),
                name.orElse(null),
                code.orElse(null));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {

        Optional<ItemCategory> itemCategoryOptional = categoryRepository.findById(id);
        if(itemCategoryOptional.isPresent()){
            ItemCategory itemCategory = itemCategoryOptional.get();
            Optional<Long> countOptional = categoryRepository.countAllByParentCategoryAndActive(
                    itemCategory,true);
            if(countOptional.isPresent() && countOptional.get() > 0){
                throw new AesException("Sorry! Unable to delete, Category already used in Child Category");
            }

            List<Long> getItem = categoryRepository.findAllExistingItemsForSubcategory(itemCategory.getId());
            if(!getItem.isEmpty()){
                throw new AesException("Sorry! This Subcategory has existing Item");
            }

            if(itemCategory.getScmCategoryId()!=null){
                throw new RuntimeException("Sorry! Category Already Synced");
            }
            if(itemCategory.getParentCategory() !=null) {
                categoryAttributeRepository.deleteByCategoryId(itemCategory.getId());
                subcategoryBrandRepository.deleteBySubcategoryId(itemCategory.getId());
            }
            itemCategory.setActive(false);
            categoryRepository.save(itemCategory);
        }
    }

    @Override
    public String getNewCategoryCode(String key, Optional<Long> categoryId) {
        String autoCode = categoryRepository.findMaxOrderById(key,categoryId.orElse(null));
//        if(icOp.isPresent()){
//            ItemCategory ic = icOp.get();
//            Long newProductId = ic.getId() + 1;
//            return String.format("%05d",newProductId);
//        }
//        return String.format("%05d",1);
        return autoCode;
    }

    @Override
    public String getNewCategoryCode(String key, String prefix, Optional<Long> categoryId) {
        String autoCode = categoryRepository.findMaxOrderById(key, prefix, categoryId.orElse(null));
        StringBuilder sb = new StringBuilder();
        sb.append(prefix.toUpperCase());
        if (categoryId.isPresent()) {
            sb.append("-").append(key.toUpperCase());
        } else {
            sb.append(key.toUpperCase());
        }
        sb.append(autoCode);
        return sb.toString();
    }

    @Override
    @Transactional
    public void deleteAttribute(Long categoryId, Long attributeId) {
        Optional<ItemCategory> icOp = categoryRepository.findById(categoryId);
        if(icOp.isPresent()){
            categoryAttributeRepository.deleteByIdAndCategoryId(attributeId,categoryId);

        }

    }

    @Override
    public Optional<ItemCategory> getCategoryForAVendor(Long vendorId, Long Id) {
       return categoryRepository.findSavedCategoryForVendor(vendorId, Id);
    }

    @Override
    public List<SubCategory> getCategoriesForVendor(Long vendorId) {
       return categoryRepository.findSCategoryForVendor(vendorId);
    }

    @Override
    public Optional<ItemCategory> findRootReferenceItem(Long Id) {
        return categoryRepository.findById(Id);
    }

    @Override
    public List<ItemCategory> existCategoryByNameIgnoreCase(String category_name) {
        return categoryRepository.findCategoryByNameIgnoreCase(category_name.toLowerCase());
    }

    @Override
    public List<ItemCategory> existCategoryBySubCatNameIgnoreCase(String category_name) {
        return categoryRepository.findCategoryBySubCatNameIgnoreCase(category_name.toLowerCase());
    }

    @Override
    public List<ItemCategory> existCategoryByParentCategoryIdSubCatNameIgnoreCase(Long parentCategoryId, String category_name) {
        return categoryRepository.findByParentCategoryIdAndNameIgnoreCase(parentCategoryId,category_name);
    }

    @Override
    public Optional<ItemCategory> getByName(String catName) {
        return categoryRepository.findByName(catName);
    }

    @Override
    public Optional<CategoryAttribute> getCategoryAttributeValueBySubCatAndAttributeType(Long subCatId,
            String attributeType) {
                return    categoryAttributeRepository.findByCategoryIdAndAttributeType(subCatId,attributeType);
        
    }

    @Override
    public void bulkImport(String token, Organization org, String userId, Long warehouseId, Long storeId,
    Long parentCategoryId, List<Long> ids) {
        List<CategoryRequestDto> categoryList = new ArrayList<>();
        for(Long catId : ids){
            Optional<ItemCategory> itemCatOps = categoryRepository.findById(catId);
            if(itemCatOps.isPresent()){
                ItemCategory itemCategory = itemCatOps.get();
                CategoryRequestDto catReqDto = new CategoryRequestDto();
                catReqDto.setAttributes(itemCategory.getAttributes().stream().map(ica->{
                    CategoryAttribute ca = new CategoryAttribute();
                    ca.setAttributeType(ica.getAttributeType());
                    ca.setAttributeValue(ica.getAttributeValue());
                    ca.setAttributeUnit(ica.getAttributeUnit());
                    return ca;
                }).collect(Collectors.toList()));
                catReqDto.setIsForCps(false);
                catReqDto.setCode(itemCategory.getCode());
                catReqDto.setName(itemCategory.getName());
                if(parentCategoryId==null){
                    catReqDto.setParentCategory(null);
                }else{
                    catReqDto.setParentCategory(new ItemCategory(parentCategoryId));
                }
                catReqDto.setVat(itemCategory.getVat());
                catReqDto.setWarehouse(new ReferenceObjectDto(warehouseId));
                catReqDto.setWarehouseStore(new ReferenceObjectDto(storeId));
                if(itemCategory.getSubcategoryBrands().size()>0){
                    catReqDto.setBrands(itemCategory.getSubcategoryBrands().stream().map(sb->{
                        return sb.getBrand().getName();
                    }).toList());
                }

                catReqDto.setCpsCategoryId(itemCategory.getId());
                categoryList.add(catReqDto);
            }
        }

        if(categoryList.size()>0){
            
            // String authToken = login(org);
            sentItemCategoryTransfer(token, userId, org, categoryList);

        }
    }

    private void sentItemCategoryTransfer(String authToken, String userId, Organization org,List<CategoryRequestDto> categoryList){
        StringBuilder sb = new StringBuilder("/item-categories");

        sb.append("/bulk-create");

        String itemCategoryTransferEndpoint = org.getScmIpAddress().concat(sb.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        BulkCategoryRequestDto bcr = new BulkCategoryRequestDto();
        bcr.setUserId(userId);
        bcr.setCategories(categoryList);
        HttpEntity<BulkCategoryRequestDto> pqPayload = new HttpEntity<>(bcr, headers);
        System.out.println(pqPayload);
        System.out.println(itemCategoryTransferEndpoint);
        ResponseEntity<Void> response = networkService.post(itemCategoryTransferEndpoint, pqPayload, Void.class);
        if (!response.getStatusCode().equals(HttpStatus.NO_CONTENT) &&
                !response.getStatusCode().equals(HttpStatus.CREATED)) {
            throw new AesException("Something wrong");
        }
    }

    private String login(Organization organization){
       
            String url = organization.getServiceIpAddress().replace("/api/v1","")
                                .concat("/authenticate");
            String username = organization.getServiceUsername();
            String password = organization.getServicePassword();
        return networkService.getAuthToken(url,username,password);
    }

    @Override
    public List<ItemCategory> getAllSubCategories(Long categoryId, Long subCategoryId) {
        return categoryRepository.findAllSubCategories(categoryId,subCategoryId);
    }

    @Override
    public List<SubCategoryBrand> getBrandsByCategoryId(Long id) {
        return subcategoryBrandRepository.findAllBySubcategoryId(id);
    }

    @Override
    public Page<?> getPendingItemCategoryList(Optional<String> name, Optional<String> code,
                                              Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(PAGE_SIZE),sort);
        return categoryRepository.findAllPendingItemCategories(name.orElse(null),code.orElse(null),pageable);
    }

    @Override
    public Page<?> getPendingSubCategoryList(Optional<Long>parentId,Optional<String> name, Optional<String> code, Optional<Integer> page, Optional<Integer> size) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(PAGE_SIZE),sort);
        return categoryRepository.findAllPendingSubCategories(parentId.orElse(null),
                name.orElse(null),code.orElse(null),
                pageable);
    }

    @Override
    public Integer getSubCategoryCount(Long id) {
        return categoryRepository.findSubCategoryCountByCategoryId(id);
    }

    @Override
    public Integer getProductQtyByCategoryAndSubCategory(Long catId, Long subCatId) {
        return categoryRepository.findProductCountByCategoryId(catId, subCatId);
    }

    @Override
    public List<?> getAllItemCategoryList(Optional<String> name, Optional<String> code) {
        return categoryRepository.findAllItemCategory(name.orElse(null),code.orElse(null));
    }


    @Override
    @Transactional
    public void mergePendingCategory(Long id, MergePendingCategoryDto mergePendingCategoryDto) {
        ItemCategory getItemCategory = categoryRepository.findById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"No such data found"));
        // boolean emptyDto = false;
        // if(mergePendingCategoryDto.getCode() == null){
        //     emptyDto = true;
        // }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(networkService.getKeycloakAccessToken(getItemCategory.getOrganization()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        MergePendingCategoryPostDto postDto = new MergePendingCategoryPostDto();
        postDto.setWarehouseStoreId(getItemCategory.getStoreTypeId());
        HttpEntity<MergePendingCategoryPostDto> mPCDtoPayload = new HttpEntity<>(postDto, headers);

        StringBuilder sb = new StringBuilder("/item-categories");
        sb.append("/approve/category/");
        sb.append(getItemCategory.getScmCategoryId());
        String itemCategoryTransferEndpoint = scmApiEndpoint.concat(sb.toString());
        System.out.println(itemCategoryTransferEndpoint);
        

        if(mergePendingCategoryDto.getMergeCategoryId() == null){
            //no merge, send with actual item ID in the URL

            
            postDto.setApproveStatus(CategoryStatus.APPROVED);
            postDto.setCode(null);
            // mergePendingCategoryDto.setCode(getItemCategory.getCode());

            if(getItemCategory.getParentCategory() != null){
                if (mergePendingCategoryDto.getParentCategory() != null){
                    Optional<ItemCategory> getItemParentCategoryOp = categoryRepository.findById(mergePendingCategoryDto.getParentCategory().getId());
                    if(getItemParentCategoryOp.isPresent()){
                        ItemCategory getItemParentCategory = getItemParentCategoryOp.get();
                        mergePendingCategoryDto.setParentCategory(new ReferenceObjectDto(getItemParentCategory.getScmCategoryId()));
                        postDto.setMergePendingCategoryDto(mergePendingCategoryDto);
                    }
                }else{
                    postDto.setMergePendingCategoryDto(mergePendingCategoryDto);
                }
               
            }else{
                postDto.setMergePendingCategoryDto(mergePendingCategoryDto);
            }
            
            // postDto.setParentCategory(new ReferenceObjectDto(getItemParentCategory.getParentCategory().getScmCategoryId()));
            // System.out.println(mPCDtoPayload);
            ResponseEntity<Void> response = networkService.put(itemCategoryTransferEndpoint,mPCDtoPayload,Void.class);
            if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                // if(!emptyDto){
                if(mergePendingCategoryDto.getCode() != null){
                    //body is not empty so update category

                    if(getItemCategory.getParentCategory() == null){
                        //It is a Category
                        getItemCategory.setCategoryStatus(CategoryStatus.APPROVED);
                        getItemCategory.setName(mergePendingCategoryDto.getName());
                        getItemCategory.setActive(true);
                    }else{
                        //It is a subcategory
                        getItemCategory.setCategoryStatus(CategoryStatus.APPROVED);
                        getItemCategory.setName(mergePendingCategoryDto.getName());
                        // getItemCategory.setCode(mergePendingCategoryDto.getCode());
                        getItemCategory.setVat(mergePendingCategoryDto.getVat());
                        
                        for (CategoryAttribute iterable_element : mergePendingCategoryDto.getAttributes()) {
                            CategoryAttribute categoryAttribute;
                            Optional<CategoryAttribute> categoryAttributeOP = categoryAttributeRepository.findById(iterable_element.getId());
                            if(categoryAttributeOP.isEmpty()){
                                categoryAttribute = new CategoryAttribute();
                            }else{
                                categoryAttribute = categoryAttributeOP.get();
                            }
                            categoryAttribute.setAttributeType(iterable_element.getAttributeType());
                            categoryAttribute.setAttributeUnit(iterable_element.getAttributeUnit());
                            categoryAttribute.setAttributeValue(iterable_element.getAttributeValue());
                            categoryAttributeRepository.save(categoryAttribute);
                        }
                        for(String iterable_element : mergePendingCategoryDto.getBrands()) {
                            Brand brand;
                            Optional<Brand> get_brand = brandRepository.findByName(iterable_element);
                            //if brand not found, create brand
                            if(!get_brand.isPresent()){
                                brand = new Brand();
                                brand.setName(iterable_element);
                                brandRepository.save(brand);
                            }else{
                                brand = get_brand.get();
                            }
                             
                            Optional<SubCategoryBrand> scbrand = subcategoryBrandRepository.findAllByBrandIdAndSubcategoryId(brand.getId(),getItemCategory.getId());
                            if(scbrand.isPresent()){
                                SubCategoryBrand scb = scbrand.get();
                                scb.setBrand(brand);
                                scb.setSubcategory(getItemCategory);
                                subcategoryBrandRepository.save(scb);
                            }else{
                                SubCategoryBrand scb = new SubCategoryBrand();
                                scb.setBrand(brand);
                                scb.setSubcategory(getItemCategory);
                                subcategoryBrandRepository.save(scb);
                            }
                        }
                        getItemCategory.setActive(true);

                    }
                }else{
                    //approve without edit
                    getItemCategory.setCategoryStatus(CategoryStatus.APPROVED);
                    getItemCategory.setActive(true);
                }
            }else{
                throw new AesException("Something wrong");
            }

        }else{
            //send post request to SCM with categoryname and code using networkservice
            ItemCategory existingItemCategory = categoryRepository.findById(mergePendingCategoryDto.getMergeCategoryId()).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"No such data found"));;

            postDto.setApproveStatus(CategoryStatus.REJECTED);
            postDto.setCode(getItemCategory.getCode());
            MergePendingCategoryDto mpcDto = new MergePendingCategoryDto();
            mpcDto.setCode(existingItemCategory.getCode());
            mpcDto.setName(existingItemCategory.getName());
            mpcDto.setVat(existingItemCategory.getVat());
            if(existingItemCategory.getParentCategory() != null){
                mpcDto.setParentCategory(new ReferenceObjectDto(existingItemCategory.getParentCategory().getScmCategoryId()));
            }
            mpcDto.setOrganization(existingItemCategory.getOrganization());
            List<SubCategoryBrand> scbPost = subcategoryBrandRepository.findAllBySubcategoryId(existingItemCategory.getId());
            List<String> bPost = new ArrayList<>();
            for (SubCategoryBrand iterable_element : scbPost) {
                bPost.add(iterable_element.getBrand().getName());
            }
            mpcDto.setBrands(bPost);
            List<CategoryAttribute> categoryAttributesPost =categoryAttributeRepository.findAllByCategoryId(existingItemCategory.getId());
            mpcDto.setAttributes(categoryAttributesPost);
            mpcDto.setMergeCategoryId(existingItemCategory.getId());
            postDto.setMergePendingCategoryDto(mpcDto);
            // System.out.println(postDto);
            ResponseEntity<Void> response = networkService.put(itemCategoryTransferEndpoint,mPCDtoPayload,Void.class);
            if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                getItemCategory.setActive(false);
                getItemCategory.setCategoryStatus(CategoryStatus.REJECTED);
            }else{
                throw new AesException("Something wrong");
            }
        }
        categoryRepository.save(getItemCategory);
    }

    @Override
    public void rejectPendingCategory(Long id) {
        ItemCategory getItemCategory = categoryRepository.findById(id).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"No such data found"));
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(networkService.getKeycloakAccessToken(getItemCategory.getOrganization()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        MergePendingCategoryPostDto mpcpDTO = new MergePendingCategoryPostDto();
        mpcpDTO.setApproveStatus(CategoryStatus.REJECTED);


        HttpEntity<MergePendingCategoryPostDto> mPCDtoPayload = new HttpEntity<>(mpcpDTO, headers);

        StringBuilder sb = new StringBuilder("/item-categories");
        sb.append("/approve/category/");
        sb.append(getItemCategory.getScmCategoryId());
        String itemCategoryTransferEndpoint = scmApiEndpoint.concat(sb.toString());

        ResponseEntity<Void> response = networkService.put(itemCategoryTransferEndpoint,mPCDtoPayload,Void.class);
        if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
            getItemCategory.setCategoryStatus(CategoryStatus.REJECTED);
            getItemCategory.setActive(false);
            categoryRepository.save(getItemCategory);
        }else{
            throw new AesException("Something wrong");
        }

    }

    @Override
    public void updateCategoryScmId(List<ImportCategoryScmIdUpdateDto> scmIdList) {
        for (ImportCategoryScmIdUpdateDto id : scmIdList) {
            Optional<ItemCategory> itemCatOp = categoryRepository.findById(id.getCategoryIdCps());
            if(itemCatOp.isPresent()){
                ItemCategory iitemCat = itemCatOp.get();
                iitemCat.setScmCategoryId(id.getCategoryIdScm());
                categoryRepository.save(iitemCat);
            }
        }
    }

}
