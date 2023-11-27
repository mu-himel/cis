package com.aes.erp.user_management.user_credential.entity;

//import com.aes.erp.user_management.entity.UserCredentialToPrivilege;
import com.aes.erp.user_management.entity.UserCredentialToRole;
import com.aes.erp.config.RegexPattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_credential")
public class UserCredential {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "emailAddress", nullable = false, unique = true)
    @Pattern(regexp = RegexPattern.EMAIL_PATTERN, message = "email id is invalid")
    @NotBlank(message = "email is mandatory")
    private String emailAddress;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private boolean active ;

    public UserCredential(long id, String emailAddress, String password, boolean active) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.password = password;
        this.active = active;
    }

    @OneToMany(mappedBy = "userCredential", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<UserCredentialToRole> userCredentialToRoles;
//
//    @OneToMany(mappedBy = "userCredential", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<UserCredentialToPrivilege> userCredentialToPrivileges;

}
