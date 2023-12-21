package com.aes.erp.inventory.controller;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.enums.CategoryHeader;
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
    private FileUploadService fileUploadService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private StoreTypeService storeTypeService;



    @PostMapping("/category")
    public ResponseEntity<?> uploadCategory(
            @RequestParam("storeTypeId") Long storeTypeId,
            @RequestParam("file") Optional<MultipartFile> file
    ) throws IOException {

        Optional<StoreType> storeTypeOp = Optional.ofNullable(storeTypeService.getById(storeTypeId));
        if(storeTypeOp.isEmpty()){
            throw new AesException("Store Type not found");
        }


        Path path = Path.of("./uploads/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());

            FileReader in = new FileReader(fileUploadResponse.getPath()+"/"+fileUploadResponse.getFilename());
            Iterable<CSVRecord> records  = CSVFormat.RFC4180.withHeader(CategoryHeader.class).parse(in);
            records.iterator().next();

            for(CSVRecord r:records){
                System.out.println(r.get("CATEGORY_NAME"));
                String catName = r.get("CATEGORY_NAME");
                List<ItemCategory> catOp = categoryService.existCategoryByNameIgnoreCase(catName);
                if(catOp.size()==0){
                    String code = categoryService.getNewCategoryCode();
                    CategoryRequestDto categoryRequestDto = new CategoryRequestDto();
                    categoryRequestDto.setName(catName);
                    categoryRequestDto.setCode(code);
                    categoryRequestDto.setStoreType(storeTypeOp.get());
                    categoryService.addCategory(categoryRequestDto);
                }
            }
        }
        return new ResponseEntity<>(fileUploadResponse,HttpStatus.OK);
    }
}
