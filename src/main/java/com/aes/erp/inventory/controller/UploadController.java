package com.aes.erp.inventory.controller;

import com.aes.erp.exception.AesException;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.service.BulkUploadService;
import com.aes.erp.inventory.service.StoreTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/item-categories/bulk-upload")
public class UploadController {



    @Autowired
    private StoreTypeService storeTypeService;

    @Autowired
    private BulkUploadService bulkUploadService;

    @PostMapping("/category")
    public ResponseEntity<?> uploadCategory(
            // @RequestParam("storeTypeId") Long storeTypeId,
            @RequestPart("file") Optional<MultipartFile> file
    ) throws IOException {

        // Optional<StoreType> storeTypeOp = Optional.ofNullable(storeTypeService.getById(storeTypeId));
        // if(storeTypeOp.isEmpty()){
        //     throw new AesException("Store Type not found");
        // }
        bulkUploadService.categoryBulkUpload(
            // storeTypeOp,
            file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/sub-category")
    public ResponseEntity<?> uploadSubCategory(
            @RequestPart("file") Optional<MultipartFile> file
    ) throws IOException {

        bulkUploadService.subCategoryBulkUpload(file);
        return  new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
