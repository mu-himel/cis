package com.aes.erp.module_access.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "module_access_verifier_configs")
public class ModuleAccessVerifierConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private ModuleAccessPermission moduleAccessPermission;

    @ManyToOne
    @JsonIgnore
    private ModuleAccess moduleAccess;

    private String criteriaGroup;

    private String criteriaColumn;

    private String criteriaIdValue;
    private String criteriaText;
    private Integer level;
}
