package com.aes.erp.inventory.controller;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.dto.request.ActivateItemDto;
import com.aes.erp.inventory.dto.request.BulkItemGenerateDto;
import com.aes.erp.inventory.dto.request.ItemRequestDto;
import com.aes.erp.inventory.dto.request.bulk_gen.BulkGenConfigDto;
import com.aes.erp.inventory.dto.request.bulk_gen.SearchInactiveProduct;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.service.BulkItemGenerationProcessService;
import com.aes.erp.inventory.service.ItemService;
import com.aes.erp.inventory.service.TempItemService;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private TempItemService tempItemService;

    @Autowired
    private BulkItemGenerationProcessService bulkItemGenerationService;



    @PostMapping
    @ApiOperation(value = "Create Item")
    public ResponseEntity<?> addItem(
        @RequestAttribute ClaimResponseDto loggedInUser,
        @RequestBody @Valid ItemRequestDto itemRequestDto){
        itemService.createItem(loggedInUser, itemRequestDto);
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
                                      @RequestParam("subCategoryId") Optional<Long> subCategoryId,
                                      @RequestParam("storeTypeId") Optional<Long> storeTypeId
    ){

        return new ResponseEntity<>(
                itemService.getAllItems(page,size, name,code,reorderPercentage,stockThresholdQty,
                        categoryId,subCategoryId, storeTypeId),
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
                itemService.getItemDetail(id).orElse(null),
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

    @GetMapping("/sub-category/{subCatcode}")
    public ResponseEntity<?> getItemListBySubCategoryWithAttribute(@PathVariable("subCatcode") String subCatcode){
        Map<String,Object> items = new HashMap<>();
        items.put("items", itemService.getSubCategoryWiseItemListWithAttribute(subCatcode));
        return new ResponseEntity<>(
            items,    
            HttpStatus.OK
        );
    }

    

    // @PostMapping("/permutted-items")
    // public ResponseEntity<?> getMethodName(
    //     @RequestBody BulkItemGenerateDto bulkItemGenerateDto
    // ) {

    //     if(bulkItemGenerateDto.getSubCategoryId() == null){
    //         throw new AesException("Sorry! Sub Category Required");
    //     }
    //     List<ItemCategory> categories = itemService.getCategoryService().getAllSubCategories(
    //         bulkItemGenerateDto.getCategoryId(),
    //         bulkItemGenerateDto.getSubCategoryId()
    //     ); 
    //     itemService.getPermuttedItems(categories); 
    //     return new ResponseEntity<>(HttpStatus.CREATED);
    // }

    @GetMapping("/bulk-generation-status/{id}")
    public ResponseEntity<?> getBulkGenerationStatus(@PathVariable("id") Long id){
        return new ResponseEntity<>(bulkItemGenerationService.getLogByBulkProceessId(id).orElse(null),HttpStatus.OK);
    }

    @GetMapping("/inactive/{parentCategoryId}/{categoryId}")
    public ResponseEntity<?> getInactiveProducts(
        @PathVariable("parentCategoryId") Long parentCategoryId,
        @PathVariable("categoryId") Long categoryId
    ){
        return new ResponseEntity<>(itemService.getAllInactiveItems(parentCategoryId, categoryId),HttpStatus.OK);
    }

    @PostMapping("/search-inactive-products")
    public ResponseEntity<?> searchInactiveProducts(@RequestBody SearchInactiveProduct searchInactiveProduct){
        System.out.println(searchInactiveProduct);
        return new ResponseEntity<>(tempItemService.searchInactiveItems(searchInactiveProduct),HttpStatus.OK);
    }

    @PutMapping("/activate")
    public ResponseEntity<?> activateItems(@RequestBody ActivateItemDto activateItemDto){
        itemService.activateItems(activateItemDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }



    
    
}
