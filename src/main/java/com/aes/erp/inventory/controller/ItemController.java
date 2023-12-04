package com.aes.erp.inventory.controller;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.entity.Item;
import com.aes.erp.inventory.service.ItemService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @PostMapping
    @ApiOperation(value = "Create Item")
    public ResponseEntity<?> addItem(@RequestBody @Valid ItemRequestDto itemRequestDto){
        itemService.createItem(itemRequestDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Update Item")
    public ResponseEntity<?> updateItem(@ApiParam(value = "Item Id",example = "1", required = true)
                                        @PathVariable("id") Long id,
                                        @RequestBody ItemRequestDto itemRequestDto){
        itemService.updateItem(id,itemRequestDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ApiOperation(value = "Get Items with Pagination")
    public ResponseEntity<?> getItems(@RequestParam("page") Optional<Integer> page,
                                      @RequestParam("size") Optional<Integer> size,
                                      @RequestParam("name") Optional<String> name,
                                      @RequestParam("code") Optional<String> code,
                                      @RequestParam("reorderPercentage") Optional<Integer> reorderPercentage,
                                      @RequestParam("stockThresholdQty") Optional<Integer> stockThresholdQty,
                                      @RequestParam("categoryId") Optional<Long> categoryId,
                                      @RequestParam("subCategoryId") Optional<Long> subCategoryId
    ){

        return new ResponseEntity<>(
                itemService.getAllItems(page,size, name,code,reorderPercentage,stockThresholdQty,
                        categoryId,subCategoryId),
                HttpStatus.OK
        );
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get Items as List with search by name and code")
    public ResponseEntity<?> getItems(@RequestParam("categoryId") Optional<Long> categoryId,
                                      @RequestParam("name") Optional<String> name,
                                      @RequestParam("code") Optional<String> code){
        return new ResponseEntity<>(
                itemService.getAllItems(categoryId,name,code),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get Item Detail")
    public ResponseEntity<?> getItem(@ApiParam(value = "Item Id", example = "1", required = true)
                                     @PathVariable("id") Long id){
        return new ResponseEntity<>(
                itemService.getItemDetail(id),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete Item")
    public ResponseEntity<?>  deleteItem(@ApiParam(value = "Item Id", example = "1", required = true)
                                         @PathVariable("id") Long id){
        itemService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/stock-out")
//    @Transactional
    public ResponseEntity<?> stockAdd(@RequestParam("id") Long id){
        Optional<Item> item = itemService.getItemDetail(id);
        itemService.stockOut(item.get(),15);
        return new ResponseEntity<>(
                HttpStatus.OK
        );
    }

    @GetMapping("/next-id")
    @ApiOperation(value = "Get New Product Id")
    public ResponseEntity<?> getNextId(){
        Map<String,Object> response = new HashMap<>();
        response.put("code",itemService.getNextItemCode());
        return new ResponseEntity<>(
                response,
                HttpStatus.OK
        );
    }
}
