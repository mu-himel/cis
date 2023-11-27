package com.aes.erp.indent.service;

import com.aes.erp.indent.dto.request.PrIndentRequestDto;
import com.aes.erp.indent.dto.request.UpdatePrIndentDetailRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface PrIndentService {

    void createPrIndent(PrIndentRequestDto prIndentRequestDto);

    Page<?> getAllPrIndents(
            Optional<Integer> page,
            Optional<Integer> size,
            Optional<Long> categoryId,
            Optional<Long> subCategoryId,
            Optional<String> priority
    );

    List<?> getPrIndentById(
            Optional<Long> indentId
    );

    List<?> getPrIndentByIds(Optional<List<Long>> prIndentIds);

    int updateOrderDetailsOrderQty(
            UpdatePrIndentDetailRequestDto updateIndentDetailRequestDto
    );

}