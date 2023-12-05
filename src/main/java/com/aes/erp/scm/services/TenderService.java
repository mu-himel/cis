package com.aes.erp.scm.services;

import com.aes.erp.scm.DtoCollection.TenderCreateDto;
import com.aes.erp.scm.DtoCollection.TenderResponseDto;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public interface TenderService {
    void createTender(TenderCreateDto createDto);
    Page<?> getAllTenders(Optional<String> searchFilter, Optional<Integer> page,
                          Optional<Integer> size, Optional<TenderType> tenderType,
                          Optional<Long> startDate, Optional<Long> endDate);
    TenderResponseDto getTenderResponseById(Long id);
    Tender getTenderById(Long id);
}
