package com.aes.erp.scm.services;

import com.aes.erp.scm.dto.TenderCreateDto;
import com.aes.erp.scm.dto.TenderResponseDto;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface TenderService {
    void createTender(TenderCreateDto createDto);
    Page<?> getAllTenders(Optional<String> searchFilter, Optional<Integer> page,
                          Optional<Integer> size, Optional<TenderType> tenderType,
                          Optional<Long> startDate, Optional<Long> endDate);
    TenderResponseDto getTenderResponseById(Long id);
    Tender getTenderById(Long id);
    Page<?> getAllTenderProjection(Optional<String> searchFilter, Optional<Integer> page,
                                   Optional<Integer> size, Optional<TenderType> tenderType,
                                   Optional<Long> startDate, Optional<Long> endDate);
    List<?> getNegotiationHistories(Long id);
}
