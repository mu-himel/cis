package com.aes.erp.user_management.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class RoleToPrivilege {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    private Role role;
    @ManyToOne(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.DETACH})
    private Privilege privilege;

}
