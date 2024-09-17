package com.aes.erp.inventory.entity;

import com.aes.erp.user_management.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    private String name;
    private OrganizationStatus status;

    private String serviceIpAddress;
    private String scmIpAddress;
    private String serviceUsername;
    private String servicePassword;


    @OneToOne(fetch = FetchType.EAGER)
    private Role role;


    public Organization(Long id) {
        this.id = id;
    }

    
}
