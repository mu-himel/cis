package com.aes.erp.inventory.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.dto.request.CategoryRequestDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.StoreType;
import com.aes.erp.inventory.enums.CategoryHeader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class BulkUploadServiceImpl implements BulkUploadService{

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private CategoryService categoryService;

    public void categoryBulkUpload(Optional<StoreType> storeTypeOp,
                                   Optional<MultipartFile> file
    ) throws IOException {
        Path path = Path.of("./uploads/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());

            Iterable<CSVRecord> records = getCategoryRecords(fileUploadResponse);

            for(CSVRecord r:records){
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
    }


    @Override
    public void subCategoryBulkUpload(Long categoryId, Optional<StoreType> storeTypeOp, Optional<MultipartFile> file) throws IOException {

        Optional<ItemCategory> catOp = categoryService.getItemCategory(categoryId);
        if(catOp.isEmpty()){
            throw new AesException("Category not found");
        }

        Path path = Path.of("./uploads/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());
            Iterable<CSVRecord> records = getSubCategoryRecords(fileUploadResponse);
        }
    }

    private Iterable<CSVRecord> getCategoryRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath()+"/"+fileUploadResponse.getFilename());
        Iterable<CSVRecord> records  = CSVFormat.RFC4180.withHeader(CategoryHeader.class).parse(in);
        records.iterator().next();
        return records;
    }
    
    private Iterable<CSVRecord> getSubCategoryRecords(FileUploadResponse fileUploadResponse) throws IOException {
        FileReader in = new FileReader(fileUploadResponse.getPath()+"/"+fileUploadResponse.getFilename());
        Iterable<CSVRecord> records  = CSVFormat.RFC4180.withHeader(CategoryHeader.class).parse(in);
        records.iterator().next();
        return records;
    }
}
