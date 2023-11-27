package com.aes.erp.module_access.repository;

import com.aes.erp.module_access.entity.ModuleAccess;
import com.aes.erp.module_access.entity.ModuleAccessVerifierConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ModuleAccessVerifierConfigRepository extends JpaRepository<ModuleAccessVerifierConfig,Long> {
    Optional<ModuleAccessVerifierConfig> findAllByModuleAccessPermissionId(Long modulePermissionId);

    @Query(value = "SELECT DISTINCT mavc FROM ModuleAccessVerifierConfig mavc " +
            " WHERE mavc.moduleAccess = :moduleAccess")
    List<ModuleAccessVerifierConfig> findByModule(@Param("moduleAccess") ModuleAccess module);
}
