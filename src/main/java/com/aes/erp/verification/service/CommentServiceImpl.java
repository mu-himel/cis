package com.aes.erp.verification.service;

import com.aes.erp.authentication.dto.ClaimResponseDto;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.verification.dto.request.CommentDto;
import com.aes.erp.verification.entity.Comment;
import com.aes.erp.verification.enums.DomainType;
import com.aes.erp.verification.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

@Service
public class CommentServiceImpl implements CommentService{

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public void addComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setCommentedBy(new Employee(commentDto.getCommentedBy().getId()));
        comment.setMessage(commentDto.getMessage());
        comment.setDomainId(commentDto.getDomainId());
        comment.setDomainType(commentDto.getDomainType());
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void addComment(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public void addComment(List<Comment> comments) {
        commentRepository.saveAll(comments);
    }

    @Override
    public Comment prepareComment(ClaimResponseDto claimResponseDto,
                                  DomainType domainType,
                                  Long domainId,
                                  String msg) {
        Comment comment = new Comment();
        comment.setCommentedBy(new Employee(claimResponseDto.getEmployee().getId()));
        comment.setMessage(msg);
        comment.setDomainType(domainType);
        comment.setDomainId(domainId);
        return comment;
    }

    @Override
    public Comment prepareComment(Employee verifier, DomainType domainType, Long domainId, String msg) {
        Comment comment = new Comment();
        comment.setCommentedBy(verifier);
        comment.setDomainType(domainType);
        comment.setDomainId(domainId);
        comment.setMessage(msg);
        return comment;
    }

    @Override
    public List<?> getCommentsByDomain(DomainType domainType, Long domainId) {
        return commentRepository.findAllByDomainTypeAndDomainId(domainType,domainId);
    }


}
