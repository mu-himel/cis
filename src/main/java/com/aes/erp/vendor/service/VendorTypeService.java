package com.aes.erp.vendor.service;

import com.aes.erp.vendor.entity.VendorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface VendorTypeService {
    void createVendorType(VendorType vendorType);
    List<?> getAlLVendorTypes();

    Page<?> getAllVendorTypes(Optional<Integer> page,
                              Optional<Integer> size);

    void deleteVendorType(Long id);

    Long getVendorTypeCount();


}
