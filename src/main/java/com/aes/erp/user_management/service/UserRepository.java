package com.aes.erp.user_management.service;

import com.aes.erp.employee.enums.EmployeeType;
import com.aes.erp.user_management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailAddressIgnoreCaseStartingWith(String email);    

    Optional<User> findByEmailAddress(String emailAddress);
    @Query(value = "SELECT * FROM user,user_credential, role, user_to_team, team WHERE \n" +
            "user_credential.id =user.user_credential_id\n" +
            "       AND user_to_team.user_id = user.id\n" +
            "       AND user_to_team.team_id = team.id\n" +
            "       AND user_to_team.role_id = role.id\n" +
            "       AND role.role_name = ?1 AND user_to_team.team_id = ?2", nativeQuery = true)

    List<User> findByRolesAndTeam(String roleName, long team_id);

    List<User> findUsersByIdIn(List<Long> userIds);

    @Query(value = "SELECT organization_profile.name FROM user, user_to_organization,organization,organization_profile WHERE user.id = user_to_organization.user_id AND\n" +
            "user_to_organization.organization_id = organization.id AND organization.organization_profile_id = organization_profile.id AND user.id=?1", nativeQuery = true)
    List<String> findOrganizationNamesById(Long userId);

    @Query(value = "select * from users, user_credential, user_credential_to_role, role where users.user_credential_id = user_credential.id AND " +
            "user_credential_to_role.user_credential_id = user_credential.id AND user_credential_to_role.role_id = role.id AND role.role_name=?1", nativeQuery = true)
    List<User> findAllByRoleName(String roleName);

    @Query(value =
            "select e.id as empId, e.employee_id as employeeId , e.name as name , e.phone as phone," +
                    " e.employee_type as employeeType, users.id, users.first_name as firstName, \n" +
                    "            users.last_name as lastName, \n" +
                    "            users.email_address as emailAddress, \n" +
                    "            uc.active as active  ,r.role_name as roleName\n" +
                    "            from users\n" +
                    "            LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
                    "            LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id \n" +
                    "            LEFT JOIN role r on r.id  = uctr.role_id  \n" +
                    "            LEFT JOIN employees e on e.user_id = users.id\n" +
                    "            where r.role_name !=\"SYS_ADMIN\" AND r.role_name IN (:name)",
    countQuery = "select count(*) " +
            "            from users\n" +
            "            LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
            "            LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id \n" +
            "            LEFT JOIN role r on r.id  = uctr.role_id  \n" +
            "            LEFT JOIN employees e on e.user_id = users.id\n" +
            "            where r.role_name !=\"SYS_ADMIN\" AND r.role_name IN (:name)",
    nativeQuery = true)
    Page<UserInfo> findAllByRoleName(@Param("name") String roleName, Pageable pageable);


    @Query(value =
            "select e.id as empId, e.employee_id as employeeId , e.name as name , e.phone as phone," +
                    " e.employee_type as employeeType, users.id, users.first_name as firstName, \n" +
                    "            users.last_name as lastName, \n" +
                    "            users.email_address as emailAddress, \n" +
                    "            uc.active as active  ,r.role_name as roleName\n" +
                    "            from users\n" +
                    "            LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
                    "            LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id \n" +
                    "            LEFT JOIN role r on r.id  = uctr.role_id  \n" +
                    "            LEFT JOIN employees e on e.user_id = users.id\n" +
                    "            where r.role_name !=\"SYS_ADMIN\" AND r.role_name IN (:roleName) " +
                    " AND (:name IS NULL OR LOWER(e.name) LIKE concat(LOWER(:name),'%'))" +
                    " AND (:employeeId IS NULL OR e.employee_id LIKE concat(:employeeId,'%'))" +
                    " AND (:email IS NULL OR LOWER(users.email_address) LIKE concat(LOWER(:email),'%'))" +
                    " AND (:phone IS NULL OR e.phone LIKE concat(:phone,'%'))" +
                    " AND (:employeeType IS NULL OR e.employee_type = :employeeType)",
            countQuery = "select count(*) " +
                    "            from users\n" +
                    "            LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
                    "            LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id \n" +
                    "            LEFT JOIN role r on r.id  = uctr.role_id  \n" +
                    "            LEFT JOIN employees e on e.user_id = users.id\n" +
                    "            where r.role_name !=\"SYS_ADMIN\" AND r.role_name IN (:roleName) " +
                    " AND (:name IS NULL OR LOWER(e.name) LIKE concat(LOWER(:name),'%'))" +
                    " AND (:employeeId IS NULL OR e.employee_id LIKE concat(:employeeId,'%'))" +
                    " AND (:email IS NULL OR LOWER(users.email_address) LIKE concat(LOWER(:email),'%'))" +
                    " AND (:phone IS NULL OR e.phone LIKE concat(:phone,'%'))" +
                    " AND (:employeeType IS NULL OR e.employee_type = :employeeType)",
            nativeQuery = true)
    Page<UserInfo> findAllByRoleName(@Param("roleName") String roleName,
                                     @Param("employeeId") String employeeId,
                                     @Param("name") String name,
                                     @Param("email") String email,
                                     @Param("phone") String phone,
                                     @Param("employeeType") String employeeType,
                                     Pageable pageable);

        @Query(value =
                "select e.id as empId, e.employee_id as employeeId , e.name as name , e.phone as phone," +
                        " e.employee_type as employeeType, users.id, users.first_name as firstName, \n" +
                        "            users.last_name as lastName, \n" +
                        "            users.email_address as emailAddress, \n" +
                        "            uc.active as active  ,r.role_name as roleName\n" +
                        "            from users\n" +
                        "            LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
                        "            LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id \n" +
                        "            LEFT JOIN role r on r.id  = uctr.role_id  \n" +
                        "            LEFT JOIN employees e on e.user_id = users.id\n" +
                        "            where r.role_name IN ('EMPLOYEE','ENLISTER','AUDITOR','INVENTORY CONTROLLER')  " +
                        " AND (:name IS NULL OR LOWER(e.name) LIKE concat(LOWER(:name),'%'))" +
                        " AND (:employeeId IS NULL OR e.employee_id LIKE concat(:employeeId,'%'))" +
                        " AND (:email IS NULL OR LOWER(users.email_address) LIKE concat(LOWER(:email),'%'))" +
                        " AND (:phone IS NULL OR e.phone LIKE concat(:phone,'%'))" +
                        " AND (:employeeType IS NULL OR e.employee_type = :employeeType)",
                countQuery = "select count(*) " +
                        "            from users\n" +
                        "            LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
                        "            LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id \n" +
                        "            LEFT JOIN role r on r.id  = uctr.role_id  \n" +
                        "            LEFT JOIN employees e on e.user_id = users.id\n" +
                        "            where r.role_name IN ('EMPLOYEE','ENLISTER','AUDITOR','INVENTORY CONTROLLER')  AND (:name IS NULL OR LOWER(e.name) LIKE concat(LOWER(:name),'%'))" +
                        " AND (:employeeId IS NULL OR e.employee_id LIKE concat(:employeeId,'%'))" +
                        " AND (:email IS NULL OR LOWER(users.email_address) LIKE concat(LOWER(:email),'%'))" +
                        " AND (:phone IS NULL OR e.phone LIKE concat(:phone,'%'))" +
                        " AND (:employeeType IS NULL OR e.employee_type = :employeeType)",
                nativeQuery = true)
        Page<UserInfo> findAllEmployee( @Param("employeeId") String employeeId,
                                        @Param("name") String name,
                                        @Param("email") String email,
                                        @Param("phone") String phone,
                                        @Param("employeeType") String employeeType,
                                        Pageable pageable);

    @Query(value =  "select e.employee_id employeeId , e.name name , e.phone phone, " +
            "        users.id,users.first_name firstName, \n" +
            "        users.last_name lastName, \n" +
            "        users.email_address emailAddress, \n" +
            "        uc.active active  ,r.role_name roleName\n" +
            "        from users\n" +
            "        LEFT JOIN  user_credential uc on uc.id = users.user_credential_id\n" +
            "        LEFT JOIN user_credential_to_role uctr on uctr.user_credential_id  = uc.id\n" +
            "        LEFT JOIN role r on r.id  = uctr.role_id\n" +
            "        LEFT JOIN employees e on e.user_id = users.id\n" +
            "        where r.role_name = 'EMPLOYEE'\n" +
            "        and users.id NOT IN " +
            "           (select user_id from user_assignment ua where ua.user_id IS NOT null)",
            nativeQuery = true)
    List<UserInfo> findAllUnassignedUsers();

    @Query(value = """
            SELECT MAX(COALESCE(u.id,0))+1 FROM User u
            """)
    Long getNextId();

    interface UserInfo {
        Long getId();

        String getFirstName();

        String getLastName();

        String getEmailAddress();

        String getName();

        String getPhone();

        Long getEmpId();
        String getEmployeeId();
        EmployeeType getEmployeeType();
        Boolean getActive();

        String getRoleName();
    }
}