package com.aes.erp.inventory.service;

import com.aes.erp.inventory.entity.StoreType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface BulkUploadService {

    void categoryBulkUpload(Optional<StoreType> storeTypeOp, Optional<MultipartFile> file) throws IOException;
    void subCategoryBulkUpload(Long categoryId, Optional<StoreType> storeTypeOp, Optional<MultipartFile> file) throws IOException;
}
