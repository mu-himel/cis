package com.aes.erp.scm.services;

import com.aes.erp.scm.dto.NoteDto;
import com.aes.erp.scm.dto.TenderCreateDto;
import com.aes.erp.scm.dto.TenderResponseDto;
import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.inventory.entity.ItemCategory;
import com.aes.erp.inventory.entity.Organization;
import com.aes.erp.scm.Entities.Tender;
import com.aes.erp.scm.Entities.TenderType;
import org.springframework.data.domain.Page;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;

public interface TenderService {
    void createTender(TenderCreateDto createDto);
    Page<?> getAllTenders(ClaimResponseDto loggedInUser, Optional<String> searchFilter, Optional<Integer> page,
                          Optional<Integer> size, Optional<TenderType> tenderType,
                          Optional<Long> startDate, Optional<Long> endDate);
    TenderResponseDto getTenderResponseById(Long id);
    Tender getTenderById(Long id);

    Page<?> getAllTenderProjection(
                        ClaimResponseDto loggedInUser,
                        Optional<String> searchFilter, Optional<Integer> page,
                        Optional<Integer> size, Optional<TenderType> tenderType,
                        Optional<Long> startDate, Optional<Long> endDate
                    );

    Page<?> getAllTenderProjectionWithFilter(
        ClaimResponseDto loggedInUser,
        Optional<String> searchFilter, Optional<Integer> page,
        Optional<Integer> size, Optional<TenderType> tenderType,Optional<Long> itemQty, Optional<Long> organizationId, Optional<Long> categoryId,
        Optional<String> startDate, Optional<String> endDate
    );

    Page<?> getClosedTenderProjection(
                        ClaimResponseDto loggedInUser,
                        Optional<String> searchFilter, Optional<Integer> page,
                        Optional<Integer> size, Optional<TenderType> tenderType,
                        Optional<Long> startDate, Optional<Long> endDate
                    );
                    
    List<?> getNegotiationHistories(ClaimResponseDto loggedInUser, Long id);
    void rejectTender(ClaimResponseDto loggedInUser, Long id, NoteDto noteDto);
    Tender getTenderByRfqNo(String tenderNo);
}
