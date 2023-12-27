package com.aes.erp.vendor.service;

import com.aes.erp.fileupload.service.FileUploadService;
import com.aes.erp.vendor.entity.VendorType;
import com.aes.erp.vendor.repository.VendorTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;


@Service
public class VendorTypeServiceImpl implements VendorTypeService{

    @Autowired
    private VendorTypeRepository vendorTypeRepository;

    @Override
    public void createVendorType(VendorType vendorType) {
        vendorTypeRepository.save(vendorType);
    }

    @Override
    public List<?> getAlLVendorTypes() {
        return vendorTypeRepository.findAll();
    }

    @Override
    public Page<?> getAllVendorTypes(Optional<Integer> page,
                                     Optional<Integer> size) {

        Pageable pageable = PageRequest.of(page.orElse(0),size.orElse(10));
        return vendorTypeRepository.findAll(pageable);
    }

    @Override
    public void deleteVendorType(Long id) {
        vendorTypeRepository.deleteById(id);
    }

    @Override
    public Long getVendorTypeCount() {
        return vendorTypeRepository.count();
    }
}
