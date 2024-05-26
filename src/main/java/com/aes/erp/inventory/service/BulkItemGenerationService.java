package com.aes.erp.inventory.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aes.erp.inventory.dto.request.ActivateItemDetailDto;
import com.aes.erp.inventory.dto.request.ActivateItemDto;
import com.aes.erp.inventory.entity.BulkProcessLog;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.entity.BulkProcessLog.BulkItemStatus;
import com.aes.erp.inventory.enums.BudgetType;
import com.aes.erp.inventory.repository.BulkProcessLogRepository;
import com.aes.erp.inventory.repository.ItemRepository;

@Service
public class BulkItemGenerationService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BulkProcessLogRepository bulkProcessLogRepository;

    public Optional<BulkProcessLog> getLastLog(String name){

        return bulkProcessLogRepository.findFirstByProcessNameOrderByIdDesc(name);
    }


    @Async
    public void saveProducts(List<Item> products,BulkProcessLog bulkProcess){
        
        List<Item> filteredItems = new ArrayList<>();
        for(Item item : products){
            List<?> exists = this.getByAttributes(item.getBrand().getId(), item.getItemAttributeName());
            if(exists.size()==0){
                item.setAttributes(item.getAttributes().stream().map(attr->{
                    attr.setItem(item);
                    return attr;
                }).collect(Collectors.toList()));
                filteredItems.add(item);
                // itemRepository.save(item);
            }
        }
        itemRepository.saveAll(filteredItems);
        
        bulkProcess.setStatus(BulkItemStatus.DONE);
        bulkProcessLogRepository.saveAndFlush(bulkProcess);
    }

    private List<?> getByAttributes(Long brandId, String attribute) {
        return itemRepository.findByAttributesNotActive(brandId,attribute);
    }


    @Async
    public void activateItems(ActivateItemDto activateItemDto) {
        for(ActivateItemDetailDto itemDetailDto : activateItemDto.getItemIdList()){
            Optional<Item> itemOp = itemRepository.findById(itemDetailDto.getId());
            if(itemOp.isPresent()){
                Item item = itemOp.get();
                item.setActive(true);
                item.setCode(itemDetailDto.getCode());
                itemRepository.save(item);
            }
        }
    }
}
