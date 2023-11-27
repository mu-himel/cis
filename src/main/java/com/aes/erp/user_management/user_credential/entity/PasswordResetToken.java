package com.aes.erp.user_management.user_credential.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    private String emailAddress;

   /* @OneToOne(targetEntity = UserCredential.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "user_credential_id")
    private UserCredential userCredential;*/

    private Date expiryDate;

    public PasswordResetToken(String emailAddress, Date expiryDate, String token) {
        this.emailAddress = emailAddress;
        this.expiryDate = expiryDate;
        this.token = token;

    }
}
