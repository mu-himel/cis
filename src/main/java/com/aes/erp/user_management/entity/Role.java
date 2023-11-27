package com.aes.erp.user_management.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    /*@Pattern(regexp = ALPHABET_WITH_SPACE, message = "Role Name: Field cannot have numeric or special characters")*/
    @Column(unique = true)
    private String roleName;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<RoleToPrivilege> roleToPrivileges;
}
