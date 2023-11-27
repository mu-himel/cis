package com.aes.erp.user_management.service;

import com.aes.erp.user_management.entity.UserCredentialToRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.List;

public interface UserCredentialToRoleRepository extends JpaRepository<UserCredentialToRole, Long> {

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_credential_to_role WHERE user_credential_id=?1", nativeQuery = true)
    void deleteByUserCredentialId(Long userCredentialId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_credential_to_role WHERE user_credential_id=?1 AND role_id=?2", nativeQuery = true)
    void deleteByUserCredentialIdAndRoleId(Long userCredentialId, Long roleId);

    List<UserCredentialToRole> findUserCredentialToRoleByUserCredentialIdAndRoleId(long userCredentialId, long roleId);
    @Transactional
    @Modifying
    @Query(value = "DELETE FROM user_credential_to_role WHERE role_id=?1", nativeQuery = true)
    void deleteAllByRoleId(long id);
}
