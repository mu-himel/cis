package com.aes.erp.productrequirment.service;

import com.aes.erp.exception.AesException;
import com.aes.erp.productrequirment.dto.request.ProductRequirementRequestDto;
import com.aes.erp.productrequirment.entity.ProductRequirement;
import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
import com.aes.erp.productrequirment.repository.ProductRequirementRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ProductRequirementServiceImpl implements ProductRequirementService {
    private final ProductRequirementRepository productRequirementRepository;

    public ProductRequirementServiceImpl(ProductRequirementRepository productRequirementRepository) {
        this.productRequirementRepository = productRequirementRepository;
    }


    @Override
    public void createProductRequirement(ProductRequirementRequestDto productRequirementRequestDto) {
        ProductRequirement productRequirement = productRequirementRequestDto.getEntity();
        productRequirement.setStatus(ProductRequirmentStatus.OPEN);
        productRequirementRepository.save(productRequirement);
    }

    @Override
    public Page<?> getAllProductRequirements(Optional<Integer> page, Optional<Integer> size, Optional<Long> categoryId, Optional<Long> subCategoryId, Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Page<?> result = null;
        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
        result = productRequirementRepository.findAllProductRequirements(categoryId.orElse(null),
                subCategoryId.orElse(null),
                startDate.orElse(null),
                endDate.orElse(null),
                pageable);
        return result;
    }
    @Override
    public List<?> getAllProductRequirementView( Optional<Long> categoryId, Optional<Long> subCategoryId) {
        List<?> result = productRequirementRepository.getAllProductRequirementView(
                categoryId.orElseThrow(() ->new AesException("Category should not empty")),
                subCategoryId.orElseThrow(() ->new AesException("Sub Category should not empty"))
        );
        return result;
    }

    @Override
    public int updateStatusByCategoryAndSubCategory(
            ProductRequirmentStatus toStatus,
            ProductRequirmentStatus fromStatus,
            Long categoryId,
            Long subCategoryId
    ) {
        int result = productRequirementRepository.updateStatusByCategoryAndSubCategory(toStatus,fromStatus,categoryId,subCategoryId);
        return result;
    }



}
