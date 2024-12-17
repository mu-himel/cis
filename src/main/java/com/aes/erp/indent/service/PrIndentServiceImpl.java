//package com.aes.erp.indent.service;
//
//import com.aes.erp.exception.AesException;
//import com.aes.erp.indent.dto.request.PrIndentRequestDto;
//import com.aes.erp.indent.dto.request.UpdatePrIndentDetailRequestDto;
//import com.aes.erp.indent.entity.PrIndent;
//import com.aes.erp.indent.enums.PrIndentStatus;
//import com.aes.erp.indent.repository.PrIndentRepository;
//import com.aes.erp.productrequirment.enums.ProductRequirmentStatus;
//import com.aes.erp.productrequirment.service.ProductRequirementService;
//import com.aes.erp.verification.service.VerificationService;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@Service
//public class PrIndentServiceImpl implements PrIndentService {
//    private final ProductRequirementService productRequirementService;
//    private final PrIndentRepository prIndentRepository;
//
//    public PrIndentServiceImpl(
//            ProductRequirementService productRequirementService,
//            PrIndentRepository prIndentRepository,
//            VerificationService verificationService) {
//        this.prIndentRepository = prIndentRepository;
//        this.productRequirementService = productRequirementService;
//
//    }
//
//    @Override
//    public void createPrIndent(PrIndentRequestDto prIndentRequestDto) {
//        PrIndent prIndent = prIndentRequestDto.getEntity();
//        prIndent.setStatus(PrIndentStatus.OPEN);
//
//        //Save PrIndent
//        prIndent = prIndentRepository.save(prIndent);
//
//        //Update Product Requirement Status
//        productRequirementService.updateStatusByCategoryAndSubCategory(ProductRequirmentStatus.CLOSE,
//                ProductRequirmentStatus.OPEN,
//                prIndent.getCategory().getId(),
//                prIndent.getSubCategory().getId());
//
//    }
//
//    @Override
//    public Page<?> getAllPrIndents(Optional<Integer> page,
//                                   Optional<Integer> size,
//                                   Optional<Long> categoryId,
//                                   Optional<Long> subCategoryId,
//                                   Optional<String> priority) {
//        Sort sort = Sort.by(Sort.Direction.DESC, "id");
//        Pageable pageable = PageRequest.of(page.orElse(0), size.orElse(10), sort);
//        Page<?> result = prIndentRepository.getAllPrIndents(
//                categoryId.orElse(null),
//                subCategoryId.orElse(null),
//                priority.orElse(null),
//                pageable);
//        return result;
//    }
//
//    @Override
//    public List<?> getPrIndentById(Optional<Long> prIndentId) {
//        List<?> result = prIndentRepository.getPrIndentById(
//                prIndentId.orElseThrow(() -> new AesException("PrIndent id should not empty"))
//        );
//        return result;
//    }
//
//    @Override
//    public List<?> getPrIndentByIds(Optional<List<Long>> prIndentIds) {
//
//
//        List<?> result = prIndentRepository.getPrIndentByIds(
//                prIndentIds.orElseThrow(() -> new AesException("PrIndent id should not empty"))
//        );
//        return result;
//    }
//
//
//    @Transactional
//    @Override
//    public int updateOrderDetailsOrderQty(
//            UpdatePrIndentDetailRequestDto updatePrIndentDetailRequestDto
//    ) {
//
//        if (CollectionUtils.isEmpty(updatePrIndentDetailRequestDto.getPrIndentDetails())) {
//            throw new AesException("PrIndents should not empty");
//        }
//        AtomicInteger result = new AtomicInteger();
//        updatePrIndentDetailRequestDto.getPrIndentDetails().forEach(x -> {
//            result.addAndGet(
//                    prIndentRepository.updatePrIndentDetailsOrderQty(
//                            x.orderQty(),
//                            x.id(),
//                            updatePrIndentDetailRequestDto.getId()
//                    ));
//        });
//
//        return result.get();
//    }
//
//
//}
