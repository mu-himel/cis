package com.aes.erp.inventory.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.aes.erp.inventory.dto.request.ActivateItemDetailDto;
import com.aes.erp.inventory.dto.request.ActivateItemDto;
import com.aes.erp.inventory.entity.BulkGenBrand;
import com.aes.erp.inventory.entity.BulkItemGenConfig;
import com.aes.erp.inventory.entity.BulkProcessLog;
import com.aes.erp.inventory.entity.BulkProcessLog.BulkItemStatus;
import com.aes.erp.inventory.entity.CategoryAttribute;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.ItemAttribute;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.TempItem;
import com.aes.erp.inventory.entity.TempItemAttribute;
import com.aes.erp.inventory.repository.BulkProcessLogRepository;
import com.aes.erp.inventory.repository.ItemRepository;
import com.aes.erp.inventory.repository.TempItemRepository;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@Service
public class BulkItemGenerationProcessServiceImpl implements BulkItemGenerationProcessService{
    
    @Autowired
    private TempItemRepository tempItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BulkProcessLogRepository bulkProcessLogRepository;



    @Autowired
    private CategoryService categoryService;


    public Optional<BulkProcessLog> getLastLog(String name){

        return bulkProcessLogRepository.findFirstByProcessNameOrderByIdDesc(name);
    }


    @Async
    public void saveProducts(List<TempItem> products,BulkProcessLog bulkProcess){
        
//        List<TempItem> filteredItems = new ArrayList<>();
        for(TempItem item : products){
            String newCode = getNextItemCode(item.getCode());
            item.setCode(item.getCode()+"-"+newCode);
            List<?> exists = this.getByAttributes(item.getBrand().getId(), item.getItemAttributeName(), item.getItemCategory().getId());
            if(exists.size()==0){
                item.setAttributes(item.getAttributes().stream().map(attr->{
                    attr.setItem(item);
                    return attr;
                }).collect(Collectors.toList()));
//                filteredItems.add(item);
                tempItemRepository.save(item);
            }
        }
//        tempItemRepository.saveAll(filteredItems);
        
        bulkProcess.setStatus(BulkItemStatus.DONE);
        bulkProcessLogRepository.saveAndFlush(bulkProcess);
    }

    private String getNextItemCode(String prefix) {
        Long autoCode = tempItemRepository.findNextCodeByCount(prefix);
        if (autoCode != null) {
            return String.format("%08d", ++autoCode);
        }
        return String.format("%08d", 1);
    }

    private List<?> getByAttributes(Long brandId, String attribute, Long subCatId) {
        return itemRepository.findByAttributes(brandId, attribute, subCatId);
    }


    @Transactional
    public void activateItems(ActivateItemDto activateItemDto) {
        for (ActivateItemDetailDto itemDetailDto : activateItemDto.getItemIdList()) {
            Optional<TempItem> tempItemOp = tempItemRepository.findById(itemDetailDto.getId());
            if (tempItemOp.isPresent()) {
                TempItem tempItem = tempItemOp.get();
                Optional<Item> itemOp = copyItemFromTempItem(tempItem);
                if (itemOp.isPresent()) {
                    Item item = itemOp.get();
                    itemRepository.save(item);
                }
                tempItem.setActive(true);
            }
        }
    }

    private Optional<Item> copyItemFromTempItem(TempItem tempItem) {

        Optional<Item> itemExist = itemRepository.findByItemCategoryIdAndItemAttributeNameAndNameAndActive(tempItem.getItemCategory().getId(),
                tempItem.getItemAttributeName(), tempItem.getName(), true);
        if (itemExist.isEmpty()) {
            Item item = new Item();
            item.setCode(tempItem.getCode());
            item.setItemAttributeName(tempItem.getItemAttributeName());
            item.setActive(true);
            item.setItemCategory(tempItem.getItemCategory());
            item.setItemParentCategory(tempItem.getItemParentCategory());
            item.setBrand(tempItem.getBrand());
            item.setName(tempItem.getName());
            item.setAttributes(tempItem.getAttributes().stream().map(tia -> {
                ItemAttribute itemAttribute = new ItemAttribute();
                itemAttribute.setAttributeType(tia.getAttributeType());
                itemAttribute.setAttributeUnit(tia.getAttributeUnit());
                itemAttribute.setAttributeValue(tia.getAttributeValue());
                itemAttribute.setItem(item);
                return itemAttribute;
            }).collect(Collectors.toList()));

            return Optional.of(item);
        }
        return Optional.empty();
    }

    @Override
    public void getPermuttedItems(BulkItemGenConfig config, List<ItemCategory> categories) {

        List<TempItem> products = new ArrayList<>();
        for(ItemCategory cat : categories){
            List<List<String>> attributes = new ArrayList<>();
            int i=0;
            for(CategoryAttribute catAttr : cat.getAttributes()){
                String attrType = catAttr.getAttributeType();
                List<String>  attrValues = List.of(catAttr.getAttributeValue().split(","));
                List<String> _attrValues  = attrValues = attrValues.stream().map(attrV->{
                    return attrType.trim() + " " + attrV.trim() + " " + catAttr.getAttributeUnit().trim();
                }).collect(Collectors.toList());
                // map.put(i,_attrValues);
                attributes.add(_attrValues);
                i++;
            }

            List<ImmutableList<String>> immutableElements = makeListofImmutable(attributes);
            List<List<String>> cartesianProduct = Lists.cartesianProduct(immutableElements);
            prepareProducts(config.getBrands(),cat,  cartesianProduct, products);
            
            // products
        }
        
        BulkProcessLog bulkProcess = new BulkProcessLog();
        bulkProcess.setBulkItemGenConfig(config);
        bulkProcess.setStatus(BulkItemStatus.PROCESSING);
        bulkProcess.setProcessName("ITEM");
        bulkProcessLogRepository.saveAndFlush(bulkProcess);
        saveProducts(products,bulkProcess);
        System.out.println("Here");
        
    }

    

    @Override
    public Optional<Map<String,Object>> getLogByBulkProceessId(Long id) {
        Optional<BulkProcessLog> bulkItemGenOp = bulkProcessLogRepository.findByBulkItemGenConfigId(id);
        Map<String,Object> data = new HashMap<>();
        data.put("status", bulkItemGenOp.get().getStatus());
        return Optional.ofNullable(data);
    }


    private static List<ImmutableList<String>> makeListofImmutable(List<List<String>> values) {
        List<ImmutableList<String>> converted = new LinkedList<>();
            values.forEach(array -> {
                converted.add(ImmutableList.copyOf(array));
            });
        return converted;
    }

    private List<TempItem> prepareProducts(List<BulkGenBrand> brands, ItemCategory cat, List<List<String>> cartesianProduct,List<TempItem> products){
        
        // List<SubCategoryBrand> brands = categoryService.getBrandsByCategoryId(cat.getId());
        AtomicInteger i = new AtomicInteger();
        AtomicReference<String> temp = new AtomicReference<>();
        for(BulkGenBrand brand : brands){
            ItemCategory parentCategory = cat.getParentCategory();
            LocalDate localDate = LocalDate.now();
            String month = (localDate.getMonthValue()<10)? "0"+localDate.getMonthValue() : ""+localDate.getMonthValue();
            String day = (localDate.getDayOfMonth()<10)? "0"+localDate.getDayOfMonth():""+localDate.getDayOfMonth();
            cartesianProduct.stream().forEach(cp->{
                
                TempItem item = new TempItem();
                item.setItemCategory(cat);

                item.setName(brand.getBrand().getName().trim());

//                item.setCode("C"+cat.getId()+"S"+cat.getParentCategory().getId()+"B"+brand.getId()+
//                                localDate.getYear()+month+day+getCode(i));
                item.setCode(cat.getCode());
                item.setItemParentCategory(parentCategory);
                item.setBrand(brand.getBrand());
                item.setActive(false);
                temp.set(String.join(" - ", cp));
                System.out.println(temp.get());
                item.setItemAttributeName(temp.get());
                item.setAttributes(extractAttributesFromItemAttributeName(cat, item.getItemAttributeName()));
                products.add(item);
                
            });
        }

        return products;
    }

    private String getCode(AtomicInteger i,String prefix){
        Long autoCode = itemRepository.findMaxOrderById(prefix);

        if(autoCode!=null){
            return String.format("%08d",(autoCode+i.get()));
        }
//        return String.format("%08d",1);
//        Optional<TempItem> tiOp = tempItemRepository.findMaxOrderById();
//        if(tiOp.isPresent()){
//            TempItem ti = tiOp.get();
//            Long newProductId = (ti.getId()+i.getAndIncrement());
//            return String.format("%05d",newProductId);
//        }
        return String.format("%05d",i.getAndIncrement());
    }

    private List<TempItemAttribute> extractAttributesFromItemAttributeName(ItemCategory cat, String itemAttributeName){
        String[] attrs = itemAttributeName.split(" - ");
        List<TempItemAttribute> itemAttrList = new ArrayList<>();
        
        for(String attr : attrs){
            String _attr="";
            Optional<CategoryAttribute> catAttrOp = cat.getAttributes().stream().filter(c->{
               return attr.contains(c.getAttributeType());
              
            }).findFirst();
            
            if(catAttrOp.isPresent()){
                _attr = attr.replaceFirst(catAttrOp.get().getAttributeType(),"");
            
                // String[] args = _attr.trim().split(" ");
                TempItemAttribute pia = new TempItemAttribute();
                pia.setAttributeType(catAttrOp.get().getAttributeType());
                pia.setAttributeValue(_attr.trim().replaceAll(catAttrOp.get().getAttributeUnit(), "").trim());
                pia.setAttributeUnit(catAttrOp.get().getAttributeUnit());
                itemAttrList.add(pia);
            }
        }
        return itemAttrList;
    }
}
