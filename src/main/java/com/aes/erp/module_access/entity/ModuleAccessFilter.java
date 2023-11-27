package com.aes.erp.module_access.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Data
@Table(name = "module_access_filters")

public class ModuleAccessFilter{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ModuleAccessPermission moduleAccessPermission;

    private String criteriaGroup;

    private String criteriaColumn;

    private String criteriaIdValue;
    private String criteriaText;



}
