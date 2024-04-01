package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.entity.Brand;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemRepository itemRepository;

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
                               Optional<Long> subCategoryId

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

    @Override
    @Transactional
    public void createItem(ItemRequestDto itemRequestDto) {
        Item item = itemRequestDto.getEntity();

        StringBuilder sb = new StringBuilder();

        itemRequestDto.getAttributes().stream().forEach(itemAttribute -> {
            sb.append(itemAttribute.getAttributeType()
                    +" "+itemAttribute.getAttributeValue()
                    +" "+itemAttribute.getAttributeUnit());
            sb.append(" - ");
        });

        String itemAttributeName = sb.toString().substring(0,sb.length()-3);
        Long brandId = (itemRequestDto.getBrand()!=null)? itemRequestDto.getBrand().getId() : null;
        List<?> itemExistByAttr = this.getByAttributes(brandId,itemAttributeName);
        if(itemExistByAttr.size()>0){
            throw new AesException("Sorry! Item Already exist with same attributes for this brand");
        }

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

        if(!item.getCode().equalsIgnoreCase(itemRequestDto.getCode())){
            throw new AesException("Item Code should be unique");
        }
        if(itemRequestDto.getItemCategory()!=null) {
            item.setItemCategory(itemRequestDto.getItemCategory());
        }

        if(itemRequestDto.getItemUnit()!=null) {
            item.setItemUnit(itemRequestDto.getItemUnit());
        }
        if(itemRequestDto.getStockThresholdQty()!=null) {
            item.setStockThresholdQty(itemRequestDto.getStockThresholdQty());
        }
        if(itemRequestDto.getReorderPercentage()!=null) {
            item.setReorderPercentage(itemRequestDto.getReorderPercentage());
        }
        if(itemRequestDto.getReorderPercentage()!=null) {
            item.setReorderPercentage(itemRequestDto.getReorderPercentage());
        }

        if(itemRequestDto.getAttributes()!=null && itemRequestDto.getAttributes().size()>0) {
            item.setAttributes(itemRequestDto.getAttributes().stream().map(itemAttribute -> {
                itemAttribute.setItem(item);
                return itemAttribute;
            }).collect(Collectors.toList()));
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
}
