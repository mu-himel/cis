//package com.aes.erp.productrequirment.service;
//
//import com.aes.erp.productrequirment.dto.request.ProductRequirementRequestDto;
//import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
//import org.springframework.data.domain.Page;
//
//import javax.validation.Valid;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//public interface ProductRequirementService {
//    void createProductRequirement(@Valid ProductRequirementRequestDto productRequirementRequestDto);
//
//    Page<?> getAllProductRequirements(
//            Optional<Integer> page,
//            Optional<Integer> size,
//            Optional<Long> categoryId,
//            Optional<Long> subCategoryId,
//            Optional<LocalDateTime> startDate,
//            Optional<LocalDateTime> endDate
//    );
//
//    List<?> getAllProductRequirementView(
//            Optional<Long> categoryId,
//            Optional<Long> subCategoryId
//    );
//
//    int updateStatusByCategoryAndSubCategory(
//            ProductRequirmentStatus toStatus,
//            ProductRequirmentStatus fromStatus,
//            Long categoryId,
//            Long subCategoryId
//    );
//}