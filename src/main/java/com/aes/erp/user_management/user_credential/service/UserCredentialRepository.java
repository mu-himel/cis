package com.aes.erp.user_management.user_credential.service;


import com.aes.erp.user_management.user_credential.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, Long> {
    Optional<UserCredential> findByEmailAddress(String emailAddress);
}
