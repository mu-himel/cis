package com.aes.erp.inventory.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.inventory.entity.StoreType;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface BulkUploadService {

    void categoryBulkUpload(
            // Optional<StoreType> storeTypeOp,
            Optional<MultipartFile> file) throws IOException;

    void subCategoryBulkUpload(Optional<MultipartFile> file) throws IOException;

    void productUpload(ClaimResponseDto claimResponseDto, Optional<MultipartFile> file) throws IOException;
}
