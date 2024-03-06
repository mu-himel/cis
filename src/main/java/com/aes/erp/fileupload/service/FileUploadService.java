package com.aes.erp.fileupload.service;

import com.aes.erp.fileupload.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface FileUploadService {

    FileUploadResponse uploadFile(Path path, MultipartFile multipartFile);

    Boolean validFileSize(Long fileSize, Long limit);
}
