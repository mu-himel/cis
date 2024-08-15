package com.aes.erp.inventory.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.ActivateItemDetailDto;
import com.aes.erp.inventory.dto.request.ActivateItemDto;
import com.aes.erp.inventory.dto.request.ImportItemScmIdUpdateDto;
import com.aes.erp.inventory.dto.request.ItemMergeRequestDto;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.dto.request.MergePendingCategoryPostDto;
import com.aes.erp.inventory.dto.request.MergePendingItemsDto;
import com.aes.erp.inventory.dto.request.MergePendingItemsPostDto;
import com.aes.erp.inventory.dto.request.RemoteItemRequestDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.BulkProcessLog;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.entity.PendingItemAttribute;
import com.aes.erp.inventory.entity.PendingItemRequest;
import com.aes.erp.inventory.entity.SubCategoryBrand;
import com.aes.erp.inventory.enums.CategoryStatus;
import com.aes.erp.inventory.entity.BulkProcessLog.BulkItemStatus;
import com.aes.erp.inventory.repository.BrandRepository;
import com.aes.erp.inventory.repository.BulkProcessLogRepository;
import com.aes.erp.inventory.repository.CategoryRepository;
import com.aes.erp.inventory.repository.ItemAttributeRepository;
import com.aes.erp.inventory.repository.ItemRepository;
import com.aes.erp.inventory.repository.PendingItemRequestRepository;
import com.aes.erp.inventory.repository.TempItemRepository;
import com.aes.erp.network.NetworkService;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TempItemRepository tempItemRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NetworkService networkService;

    @Value("${scm.apiEndpoint}")
    private String scmApiEndpoint;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private BulkItemGenerationProcessService bulkItemGenerationProcessService;

    @Autowired
    private BulkProcessLogRepository bulkItemRepository;

    @Autowired
    private PendingItemRequestRepository pendingItemRequestRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository itemCategoryRepo;

    @Autowired
    private ItemAttributeRepository itemAttributeRepository;

    @Override
    public CategoryService getCategoryService() {
        return this.categoryService;
    }

    @Override
    public Optional<Item> getItemDetail(Long id) {
        return itemRepository.findById(id);
    }

    @Override
    public Page<?> getAllItems(Optional<Integer> page, Optional<Integer> size,
                               Optional<String> name,
                               Optional<String> code,
                               Optional<Integer> reorderPercentage,
                               Optional<Integer> stockThresholdQty,
                               Optional<Long> categoryId,
                               Optional<Long> subCategoryId,
                               Optional<Long> storeTypeId

    ) {

        Sort sort = Sort.by(Sort.Direction.DESC,"id");
        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10),sort);
        Page<?> result  = itemRepository.findAllItems(
                name.orElse(null),
                code.orElse(null),
                reorderPercentage.orElse(null),
                stockThresholdQty.orElse(null),
                categoryId.orElse(null),
                subCategoryId.orElse(null),
                storeTypeId.orElse(null),
                pageable);


        return result;
    }

    @Override
    public List<?> getAllItems(Optional<Long> categoryId, Optional<String> name, Optional<String> code) {
        if(categoryId.isPresent()){
            List<?> result = new ArrayList<>();
            if(name.isPresent()){
                result = itemRepository
                        .findAllByActiveAndItemCategoryIdOrItemParentCategoryIdAndNameLikeIgnoreCaseOrCodeLikeIgnoreCase(
                        true,categoryId,categoryId,name.get()+"%",name.get()+"%");

            }
            return result;
        }

        if(name.isPresent() && code.isEmpty()){
            System.out.println(name.get());
            return itemRepository.findAllByActiveAndNameLikeIgnoreCaseOrItemAttributeNameLikeIgnoreCase(true,name.get()+"%","%"+name.get()+"%");
        }
        if(name.isEmpty() && code.isPresent()){
            return  itemRepository.findAllByActiveAndCodeLikeIgnoreCaseOrItemAttributeNameLikeIgnoreCase(true, code.get()+"%",name.get()+"%");
        }
        return new ArrayList<>();
    }

    private String generateItemAttributeName(List<ItemAttribute> attributes){
        StringBuilder sb = new StringBuilder();

        attributes.stream().forEach(itemAttribute -> {
            String attrType = itemAttribute.getAttributeType().trim();
            String attrValue = itemAttribute.getAttributeValue().trim();
            String attrUnit = itemAttribute.getAttributeUnit().trim();
            if(!attrType.isEmpty() && !attrValue.isEmpty() && !attrUnit.isEmpty()){
                sb.append(attrType +" "+attrValue +" "+attrUnit);
                sb.append(" - ");
            }
        });

        return (sb.isEmpty())? "" : sb.toString().substring(0,sb.length()-3);
    }

    @Override
    @Transactional
    public void createItem(ClaimResponseDto loggedInUser, ItemRequestDto itemRequestDto) {
        Item item = itemRequestDto.getEntity();

        item.setCreatedBy(new User(loggedInUser.getId()));
        String itemAttributeName = generateItemAttributeName(itemRequestDto.getAttributes());
        
        Long brandId = (itemRequestDto.getBrand()!=null)? itemRequestDto.getBrand().getId() : null;
        List<?> itemExistByAttr = this.getByAttributes(brandId,itemAttributeName);
        if(itemExistByAttr.size()>0){
            if(itemRequestDto.getOrgId() != null){
                Organization org = organizationService.getOrganizationById(itemRequestDto.getOrgId());
                Optional<Item> itemOp = itemRepository.findByCode(itemRequestDto.getCode());
                if(itemOp.isPresent()){
                    sentItem(org,itemOp.get(),itemRequestDto.getWarehouseId());
                }
                
            }else{
                throw new AesException("Sorry! "+itemExistByAttr.size()+" Items Already exist with same attributes for this brand");
            }
            
        }
        item.setItemAttributeName(itemAttributeName);

        if(itemRepository.existsByCodeAndActive(item.getCode(),true)){
            if(itemRequestDto.getOrgId() != null){
                Organization org = organizationService.getOrganizationById(itemRequestDto.getOrgId());
                sentItem(org,item,itemRequestDto.getWarehouseId());
            }else{
                throw new AesException("Sorry! Item Code should be unique");
            }
        }else{

            item.setCode(itemRequestDto.getCode());

            if(item.getItemParentCategory()==null && item.getItemCategory()==null){
                throw new AesException("Item Sub Category Missing");
            }

            if(item.getItemParentCategory()==null){
                throw new AesException("Item Main Category Missing");
            }

            if(itemRequestDto.getBrand()!=null && itemRequestDto.getBrand().getId()!=null){
                item.setBrand(new Brand(itemRequestDto.getBrand().getId()));
            }

            if(itemRequestDto.getAttributes()!=null && itemRequestDto.getAttributes().size()>0) {

                item.setAttributes(itemRequestDto.getAttributes().stream().map(itemAttribute -> {
                    itemAttribute.setItem(item);
                    return itemAttribute;
                }).collect(Collectors.toList()));
            }
            itemRepository.save(item);

            if(itemRequestDto.getOrgId() != null && itemRequestDto.getScmItemId()==null){
                Organization org = organizationService.getOrganizationById(itemRequestDto.getOrgId());
                sentItem(org,item,itemRequestDto.getWarehouseId());
            }
            if(itemRequestDto.getOrgId()!=null && itemRequestDto.getScmItemId()!=null){
                Organization org = organizationService.getOrganizationById(itemRequestDto.getOrgId());
                item.setScmItemId(itemRequestDto.getScmItemId());
                item.setOrganization(org);
                item.setActive(false);
            }
        }

    }

    private void sentItemTransfer(String authToken, Organization organization,RemoteItemRequestDto remoteItemRequestDto){
        StringBuilder sb = new StringBuilder("/items");
                
            sb.append("/").append("/receive-from-cps");
        
        String itemTransferEndpoint = organization.getServiceIpAddress().concat(sb.toString());
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RemoteItemRequestDto> pqPayload = new HttpEntity<>(remoteItemRequestDto, headers);
        ResponseEntity<Void> response = networkService.post(itemTransferEndpoint,pqPayload,Void.class);
        if(!response.getStatusCode().equals(HttpStatus.NO_CONTENT) && 
            !response.getStatusCode().equals(HttpStatus.CREATED)){
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

    private void sentItem(Organization org, Item item,Long warehouseId) {
        RemoteItemRequestDto remoteItemRequestDto = new RemoteItemRequestDto();
        remoteItemRequestDto.setAttributes(item.getAttributes());
        remoteItemRequestDto.setBrandName(item.getName());
        remoteItemRequestDto.setCode(item.getCode());
        Optional<ItemCategory> cateOp = categoryService.getItemCategory(item.getItemCategory().getId());
        if(cateOp.isEmpty()){
            throw new AesException("Sorry! Sub Category not found");
        }
        remoteItemRequestDto.setCategoryCode(cateOp.get().getCode());
        remoteItemRequestDto.setCurrentStockQty(new BigDecimal(0l));
        remoteItemRequestDto.setName(item.getName());
        remoteItemRequestDto.setStockThresholdQty(0);
        ReferenceObjectDto w = new ReferenceObjectDto();
        w.setId(warehouseId);
        remoteItemRequestDto.setWarehouse(w);
        String authToken = login(org);
        sentItemTransfer(authToken, org, remoteItemRequestDto);
    }

    private List<?> getByAttributes(Long brandId, String attribute) {
        return itemRepository.findByAttributes(brandId,attribute);
    }

    @Override
    @Transactional
    public void updateItem(Long id, ItemRequestDto itemRequestDto) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if(itemOptional.isEmpty()){
            throw new AesException("Item not found");
        }
        Item item = itemOptional.get();

        if(itemRequestDto.getName()!=null) {
            item.setName(itemRequestDto.getName());
        }

        itemOptional = itemRepository.findByCode(itemRequestDto.getCode());
        if(itemOptional.isPresent() && !id.equals(itemOptional.get().getId())){
            throw new AesException("Item already exist with same attributes");
        }

        if(itemRequestDto.getCode()!=null){
            item.setCode(itemRequestDto.getCode());
        }
        if(itemRequestDto.getItemCategory()!=null) {
            item.setItemCategory(itemRequestDto.getItemCategory());
        }

        if(itemRequestDto.getItemUnit()!=null) {
            item.setItemUnit(itemRequestDto.getItemUnit());
        }
        
        if(itemRequestDto.getAttributes()!=null && itemRequestDto.getAttributes().size()>0) {
            item.setAttributes(itemRequestDto.getAttributes().stream().map(itemAttribute -> {
                itemAttribute.setItem(item);
                return itemAttribute;
            }).collect(Collectors.toList()));

            item.setItemAttributeName(generateItemAttributeName(item.getAttributes()));
        }
        itemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        Optional<Item> itemOptional = itemRepository.findById(id);
        if(itemOptional.isPresent()) {
            Item item = itemOptional.get();
            if(item.getIsSyncronized() != null && item.getIsSyncronized()){
                throw new AesException("Sorry! Not possible to delete this item is syncronized with erp system");
            }
            item.setActive(false);
            itemRepository.save(item);
        }
    }


    @Override
    public String getNextItemCode() {
        Optional<Item> itemOp = itemRepository.findMaxOrderById();
        if(itemOp.isPresent()){
            Item item = itemOp.get();
            Long newProductId = item.getId() + 1;
            return String.format("%05d",newProductId)+"-"+generateRandomNumber(5);
        }
        return String.format("%05d",1);
    }
    public String generateRandomNumber(int limit){
        StringBuilder sb = new StringBuilder();
        for(int i=0;i<limit;i++){
            sb.append(((int)Math.floor(Math.random()*9))+1);
        }
        return sb.toString();
    }

    @Override
    public List<?> getSubCategoryWiseItemListWithAttribute(String subCatcode) {
        List<Item> items = itemRepository.findAllByItemCategoryIdAndActive(subCatcode,true);
        return items;
    }

    
    


    

    private List<ItemAttribute> extractAttributesFromItemAttributeName(ItemCategory cat, String itemAttributeName){
        String[] attrs = itemAttributeName.split(" - ");
        List<ItemAttribute> pendingItemAttrList = new ArrayList<>();
        
        
        for(String attr : attrs){
            String _attr="";
            Optional<CategoryAttribute> catAttrOp = cat.getAttributes().stream().filter(c->{
               return attr.contains(c.getAttributeType());
              
            }).findFirst();
            
            if(catAttrOp.isPresent()){
                _attr = attr.replace(catAttrOp.get().getAttributeType(),"");
            
                // String[] args = _attr.trim().split(" ");
                ItemAttribute pia = new ItemAttribute();
                pia.setAttributeType(catAttrOp.get().getAttributeType());
                pia.setAttributeValue(_attr.trim().replaceAll(catAttrOp.get().getAttributeUnit(), "").trim());
                pia.setAttributeUnit(catAttrOp.get().getAttributeUnit());
                pendingItemAttrList.add(pia);
            }
        }
        return pendingItemAttrList;
    }
    
    

    @Override
    public List<?> getAllInactiveItems(Long parentCategoryId, Long categoryId) {
        return tempItemRepository.findAllInactiveItems(parentCategoryId, categoryId);
    }

    @Override
    public void activateItems(ActivateItemDto activateItemDto) {
        bulkItemGenerationProcessService.activateItems(activateItemDto);
        
        
    }


    @Override
    @Transactional
    public void mergePendingItems(Long id, MergePendingItemsDto mergePendingItemsDto) {
        PendingItemRequest pendingItem = pendingItemRequestRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NO_CONTENT,"No Such Entry Found"));
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(networkService.getKeycloakAccessToken(pendingItem.getOrganization()));
        headers.setContentType(MediaType.APPLICATION_JSON);
        MergePendingItemsPostDto postDto = new MergePendingItemsPostDto();
        ItemMergeRequestDto itemMergeRequestDto = new ItemMergeRequestDto();
        HttpEntity<MergePendingItemsPostDto> mPCDtoPayload = new HttpEntity<>(postDto, headers);

        StringBuilder sb = new StringBuilder("/items");
        sb.append("/approve/");
        sb.append(pendingItem.getScmItemId());

        String itemTransferEndpoint = scmApiEndpoint.concat(sb.toString());
        System.out.println(itemTransferEndpoint);
        if (mergePendingItemsDto.getMergeItemId() == null){
            //No Merge
            postDto.setApproveStatus("APPROVED");
            postDto.setCode(null);
            postDto.setWarehouseId(pendingItem.getWarehouseId());
            ItemCategory itemCat =  itemCategoryRepo.findById(pendingItem.getCategory().getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NO_CONTENT,"no such entry"));
            itemMergeRequestDto.setItemParentCategory(new ItemCategory(itemCat.getScmCategoryId()));
            ItemCategory itemSubCat =  itemCategoryRepo.findById(pendingItem.getSubCategory().getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NO_CONTENT,"no such entry"));
            itemMergeRequestDto.setItemCategory(new ItemCategory(itemSubCat.getScmCategoryId()));
            itemMergeRequestDto.setAttributes(mergePendingItemsDto.getAttributes());
            itemMergeRequestDto.setCode(mergePendingItemsDto.getCode());
            itemMergeRequestDto.setItemUnit(mergePendingItemsDto.getItemUnit());
            itemMergeRequestDto.setName(mergePendingItemsDto.getName());
            String atrName = "";
            
            
            Item approvedItem = new Item();
            approvedItem.setActive(true);
            approvedItem.setScmItemId(pendingItem.getScmItemId());
            approvedItem.setOrganization(pendingItem.getOrganization());
            Brand brandId = pendingItem.getBrand();
            if(mergePendingItemsDto.getCode() == null){
                //without body
                approvedItem.setCode(pendingItem.getCode());
                approvedItem.setItemUnit(pendingItem.getItemUnit());
                approvedItem.setItemCategory(pendingItem.getSubCategory());
                approvedItem.setItemParentCategory(pendingItem.getCategory());
                approvedItem.setBrand(pendingItem.getBrand());
                approvedItem.setItemAttributeName(pendingItem.getItemAttributeName());
                approvedItem.setName(brandId.getName());
                
                
                // Optional<PendingItemAttribute> pendingItemAtr = pendingItemRequestRepository.findById(pendingItem.getId());
                // if(pendingItemAtr.isPresent()){
                //     ItemAttribute approvItemAtr = pendingItemAtr.get();
                //     approvItemAtr.setAttributeType(iterable_element.getAttributeType());
                //     approvItemAtr.setAttributeUnit(iterable_element.getAttributeUnit());
                //     approvItemAtr.setAttributeValue(iterable_element.getAttributeValue());
                //     approvItemAtr.setItem(approvedItem);
                //     itemAttributeRepository.save(approvItemAtr);
                // }

                itemMergeRequestDto.setItemAttributeName(atrName);
                itemMergeRequestDto.setBrand(brandId.getName());
                // postDto.setItemMergeRequestDto(itemMergeRequestDto);
                ResponseEntity<Void> response = networkService.put(itemTransferEndpoint,mPCDtoPayload,Void.class);
                if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                    
                    itemRepository.save(approvedItem);

                    List<PendingItemAttribute> pattrs= pendingItem.getAttributes();
                    for (PendingItemAttribute pendingItemAttribute : pattrs) {
                        ItemAttribute approvItemAtr = new ItemAttribute();
                        approvItemAtr.setAttributeType(pendingItemAttribute.getAttributeType());
                        approvItemAtr.setAttributeUnit(pendingItemAttribute.getAttributeUnit());
                        approvItemAtr.setAttributeValue(pendingItemAttribute.getAttributeValue());
                        approvItemAtr.setItem(approvedItem);
                        itemAttributeRepository.save(approvItemAtr);
                    }
                    pendingItemRequestRepository.deleteById(pendingItem.getId());

                }else{
                    throw new AesException("Something wrong");
                }
            }else{
                //with body
                
                approvedItem.setCode(mergePendingItemsDto.getCode());
                approvedItem.setName(mergePendingItemsDto.getName());
                approvedItem.setItemUnit(mergePendingItemsDto.getItemUnit());
                approvedItem.setItemCategory(mergePendingItemsDto.getItemCategory());
                approvedItem.setItemParentCategory(mergePendingItemsDto.getItemParentCategory());
                // List<ItemAttribute> itemAtrList = new ArrayList<>();
                for (ItemAttribute iterable_element : mergePendingItemsDto.getAttributes()) {
                    atrName = atrName +" "+iterable_element.getAttributeType() +" "+ iterable_element.getAttributeValue() +" "+ iterable_element.getAttributeUnit();
                }
                // approvedItem.setAttributes(itemAtrList);
                approvedItem.setItemAttributeName(atrName);
                approvedItem.setBrand(mergePendingItemsDto.getBrand());

                itemMergeRequestDto.setItemAttributeName(atrName);
                itemMergeRequestDto.setBrand(brandId.getName());
                postDto.setItemMergeRequestDto(itemMergeRequestDto);
                
                ResponseEntity<Void> response = networkService.put(itemTransferEndpoint,mPCDtoPayload,Void.class);
                if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                    // for (ItemAttribute itemAttribute2 : itemAtrList) {
                    //     itemAttributeRepository.save(itemAttribute2);
                    // }
                    
                    itemRepository.save(approvedItem);
                    for (ItemAttribute iterable_element : mergePendingItemsDto.getAttributes()) {
                        ItemAttribute itemAttribute = new ItemAttribute();
                        itemAttribute.setAttributeType(iterable_element.getAttributeType());
                        itemAttribute.setAttributeUnit(iterable_element.getAttributeUnit());
                        itemAttribute.setAttributeValue(iterable_element.getAttributeValue());
                        itemAttribute.setItem(approvedItem);
                        itemAttributeRepository.save(itemAttribute);
                    }
                    pendingItemRequestRepository.deleteById(pendingItem.getId());

                }else{
                    throw new AesException("Something wrong");
                }
            }

        }else{
            //Merge with existing item 
            Item existingItem = itemRepository.findById(mergePendingItemsDto.getMergeItemId()).orElseThrow( ()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"No such data found"));;

            postDto.setApproveStatus("REJECTED");
            postDto.setCode(existingItem.getCode());
            postDto.setWarehouseId(pendingItem.getWarehouseId());
            itemMergeRequestDto.setName(existingItem.getName());
            itemMergeRequestDto.setCode(existingItem.getCode());
            ItemCategory itemCat =  itemCategoryRepo.findById(existingItem.getItemParentCategory().getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NO_CONTENT,"no such entry"));
            itemMergeRequestDto.setItemParentCategory(new ItemCategory(itemCat.getScmCategoryId()));
            ItemCategory itemSubCat =  itemCategoryRepo.findById(existingItem.getItemCategory().getId()).orElseThrow(()-> new ResponseStatusException(HttpStatus.NO_CONTENT,"no such entry"));
            itemMergeRequestDto.setItemCategory(new ItemCategory(itemSubCat.getScmCategoryId()));
            itemMergeRequestDto.setItemUnit(existingItem.getItemUnit());
            itemMergeRequestDto.setBrand(existingItem.getBrand().getName());
            itemMergeRequestDto.setItemAttributeName(existingItem.getItemAttributeName());
            List<ItemAttribute> itemAttributesPost =itemAttributeRepository.findAllByItemId(existingItem.getId());
            itemMergeRequestDto.setAttributes(itemAttributesPost);

            postDto.setItemMergeRequestDto(itemMergeRequestDto);
            ResponseEntity<Void> response = networkService.put(itemTransferEndpoint,mPCDtoPayload,Void.class);
            if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
                pendingItemRequestRepository.deleteById(pendingItem.getId());
            }else{
                throw new AesException("Something wrong");
            }
        }
    }

    @Override
    public void updateItemScmId(List<ImportItemScmIdUpdateDto> itemScmIdList) {
        for (ImportItemScmIdUpdateDto getItem : itemScmIdList) {
            Optional<Item> getItemOp = itemRepository.findById(getItem.getItemIdCps());
            if(getItemOp.isPresent()){
                Item pickItem = getItemOp.get();
                pickItem.setScmItemId(getItem.getItemIdScm());
                itemRepository.save(pickItem);
            }
        }
    }
    

    
}
