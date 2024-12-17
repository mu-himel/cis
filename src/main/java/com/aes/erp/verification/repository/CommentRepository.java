package com.aes.erp.verification.repository;

import com.aes.erp.verification.entity.Comment;
import com.aes.erp.verification.enums.DomainType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<CommentInfo> findAllByDomainTypeAndDomainId(DomainType domainType, Long domainId);

    interface CommentInfo{
        Long getId();

        String getMessage();

//        VerificationRepository.EmployeeInfo getCommentedBy();
    }
}
