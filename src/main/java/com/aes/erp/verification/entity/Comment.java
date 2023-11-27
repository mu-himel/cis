package com.aes.erp.verification.entity;

import com.aes.erp.employee.entity.Employee;
import com.aes.erp.verification.enums.DomainType;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long domainId;

    @Enumerated(EnumType.STRING)
    private DomainType domainType;

    @ManyToOne
    private Employee commentedBy;

    private String message;

    @CreationTimestamp
    private LocalDateTime createdAt;


}
