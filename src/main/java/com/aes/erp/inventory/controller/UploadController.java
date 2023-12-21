package com.aes.erp.inventory.controller;

import com.aes.erp.fileupload.dto.FileUploadResponse;
import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.inventory.enums.CategoryHeader;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/item-categories/bulk-upload")
public class UploadController {

    @Autowired
    private FileUploadService fileUploadService;



    @PostMapping("/category")
    public ResponseEntity<?> uploadCategory(
            @RequestParam("file") Optional<MultipartFile> file
    ) throws IOException {
        Path path = Path.of("./uploads/inventory-control");
        FileUploadResponse fileUploadResponse = null;
        if(file.isPresent()) {
            fileUploadResponse = fileUploadService.uploadFile(path, file.get());

            FileReader in = new FileReader(fileUploadResponse.getPath()+"/"+fileUploadResponse.getFilename());
            Iterable<CSVRecord> records  = CSVFormat.RFC4180.withHeader(CategoryHeader.class).parse(in);
            records.iterator().next();
            for(CSVRecord r:records){
                System.out.println(r.get("CATEGORY_NAME"));
            }
        }
        return new ResponseEntity<>(fileUploadResponse,HttpStatus.OK);
    }
}
