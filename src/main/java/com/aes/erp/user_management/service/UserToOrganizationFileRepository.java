//package com.aes.erp.user_management.service;
//
////import com.aes.erp.user_management.entity.UserToOrganizationFile;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface UserToOrganizationFileRepository extends JpaRepository<UserToOrganizationFile, Long> {
//
//    @Query(value = "SELECT u.id AS id, u.first_name AS firstName, u.last_name AS lastName,u.email_address AS emailAddress, uc.active AS status  FROM user u, user_credential uc WHERE u.id IN (SELECT user_to_organization_file.user_id FROM user_to_organization_file, fs_object where user_to_organization_file.organization_file_id=fs_object.id and fs_object.id=?1) and u.user_credential_id = uc.id", nativeQuery = true)
//    List<?> findAllByOrganizationFileId(long organizationFileId);
//
//    List<UserToOrganizationFile> findByUserId(long userId);
//
//}