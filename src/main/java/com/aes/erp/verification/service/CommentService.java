package com.aes.erp.verification.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.verification.dto.request.CommentDto;
import com.aes.erp.verification.entity.Comment;
import com.aes.erp.verification.enums.DomainType;

import java.util.List;

public interface CommentService {

    void addComment(CommentDto comment);
    void addComment(Comment comment);

    void addComment(List<Comment> comments);

    Comment prepareComment(ClaimResponseDto claimResponseDto, DomainType domainType, Long domainId,String msg);
    Comment prepareComment(Employee verifier, DomainType domainType, Long domainId, String msg);

    List<?> getCommentsByDomain(DomainType domainType, Long domainId);
}
