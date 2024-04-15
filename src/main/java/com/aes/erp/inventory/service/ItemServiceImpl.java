package com.aes.erp.inventory.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.dto.request.RemoteItemRequestDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.inventory.repository.ItemRepository;
import com.aes.erp.network.NetworkService;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.vendor.entity.RFQ_Negotiation.Offer;

import org.springframework.beans.factory.annotation.Autowired;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NetworkService networkService;

    @Autowired
    private OrganizationService organizationService;

    

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
            return itemRepository.findAllByActiveAndNameLikeIgnoreCase(true,name.get()+"%");
        }
        if(name.isEmpty() && code.isPresent()){
            return  itemRepository.findAllByActiveAndCodeLikeIgnoreCase(true, code.get()+"%");
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
            throw new AesException("Sorry! Item Already exist with same attributes for this brand");
        }
        item.setItemAttributeName(itemAttributeName);

        if(itemRepository.existsByCode(item.getCode())){
            throw new AesException("Item code already exist");
        }

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

        if(itemRequestDto.getOrgId() != null){
            Organization org = organizationService.getOrganizationById(itemRequestDto.getOrgId());
            sentItem(org,item,itemRequestDto.getWarehouseId());
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
            if(item.getIsSyncronized()){
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
            return String.format("%05d",newProductId);
        }
        return String.format("%05d",1);
    }

    @Override
    public List<?> getSubCategoryWiseItemListWithAttribute(String subCatcode) {
        List<Item> items = itemRepository.findAllByItemCategoryIdAndActive(subCatcode,true);
        return items;
    }

    
}
