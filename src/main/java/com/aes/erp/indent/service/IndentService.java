package com.aes.erp.indent.service;

import com.aes.erp.indent.dto.request.IndentRequestDto;
import com.aes.erp.indent.dto.request.MoveIndentRequestDto;
import com.aes.erp.indent.dto.request.UpdatePrIndentDetailRequestDto;
import com.aes.erp.verification.service.VerificationDomainService;
import org.springframework.data.domain.Page;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

public interface IndentService extends VerificationDomainService {
    String getNextIndentNo();

    void createIndent(String token, String uri, @Valid IndentRequestDto productRequirementRequestDto);

    Page<?> getAllIndents(
            Optional<Integer> page,
            Optional<Integer> size,
            Optional<Long> categoryId,
            Optional<Long> subCategoryId,
            Optional<String> priority
    );

    List<?> getIndentById(
            Optional<Long> indentId
    );

    List<?> getIndentByIds(Optional<List<Long>> indentIds);

    int moveIndentByIds(MoveIndentRequestDto moveIndent);



}