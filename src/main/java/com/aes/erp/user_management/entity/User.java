package com.aes.erp.user_management.entity;

import com.aes.erp.user_management.user_credential.entity.UserCredential;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

import static com.aes.erp.config.RegexPattern.ALPHABET_WITH_SPACE;
import static com.aes.erp.config.RegexPattern.EMAIL_PATTERN;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false)
    private Long id;

    @Length(min = 0, max = 50, message = "First name field can be at max 50 characters long")
    private String firstName;

    @Length(min = 0, max = 50, message = "Last name field can be at max 50 characters long")
    private String lastName;

    @Column(name = "emailAddress", unique = true)
    @Pattern(regexp = EMAIL_PATTERN, message = "Email id is invalid")
    @NotBlank(message = "email is mandatory")
    private String emailAddress;

    /*@Column(name = "roles", nullable = false)
    private String roles;*/
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<UserToOrganization> userToOrganizations;

    @OneToOne(cascade = CascadeType.ALL)
    private UserCredential userCredential;

    public User(Long id) {
        this.id = id;
    }

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    @JsonIgnore
//    private List<UserToTeam> userToTeams;

//    @JsonIgnore
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    private List<UserToOrganizationFile> userToOrganizationFiles;
}
