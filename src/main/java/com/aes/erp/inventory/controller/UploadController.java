package com.aes.erp.inventory.controller;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.enums.CategoryHeader;
import com.aes.erp.inventory.service.BulkUploadService;
import com.aes.erp.inventory.service.CategoryService;
import com.aes.erp.inventory.service.StoreTypeService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
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
            @RequestParam("storeTypeId") Long storeTypeId,
            @RequestParam("file") Optional<MultipartFile> file
    ) throws IOException {

        Optional<StoreType> storeTypeOp = Optional.ofNullable(storeTypeService.getById(storeTypeId));
        if(storeTypeOp.isEmpty()){
            throw new AesException("Store Type not found");
        }
        bulkUploadService.categoryBulkUpload(storeTypeOp,file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/sub-category")
    public ResponseEntity<?> uploadSubCategory(
            @RequestParam("storeTypeId") Long storeTypeId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam("file") Optional<MultipartFile> file
    ) throws IOException {
        Optional<StoreType> storeTypeOp = Optional.ofNullable(storeTypeService.getById(storeTypeId));
        if(storeTypeOp.isEmpty()){
            throw new AesException("Store Type not found");
        }

        bulkUploadService.subCategoryBulkUpload(categoryId,storeTypeOp,file);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
