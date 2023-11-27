package com.aes.erp.authentication;

import com.aes.erp.authentication.entity.CustomUserDetails;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.employee.service.EmployeeService;
import com.aes.erp.user_management.entity.RoleToPrivilege;
//import com.aes.erp.user_management.entity.UserCredentialToPrivilege;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.entity.UserCredentialToRole;
import com.aes.erp.user_management.service.UserRepository;
import com.aes.erp.user_management.user_credential.entity.UserCredential;
import com.aes.erp.user_management.user_credential.service.UserCredentialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserCredentialRepository repository;
    private final UserRepository userRepository;

    private final EmployeeService employeeService;

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String emailAddress) throws UsernameNotFoundException {

        User user = userRepository.findByEmailAddress(emailAddress).orElse(null);
        if(user == null) {
            throw new UsernameNotFoundException("User with username [" + emailAddress + "] not found in the system");
        }


        CustomUserDetails customUserDetails = new CustomUserDetails();
        /** logger.info("user: "+user); */
        Set<GrantedAuthority> authorities = new HashSet<>();
        for(UserCredentialToRole userCredentialToRole:user.getUserCredential().getUserCredentialToRoles()) {

            String roleName = userCredentialToRole.getRole().getRoleName();
            if(roleName.equals("EMPLOYEE")){
               Optional<Employee> employeeOptional = employeeService.getEmployeeByUserId(user.getId());
               if(employeeOptional.isPresent()){
                    customUserDetails.setEmployee(employeeOptional.get());
               }
            }

            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            for(RoleToPrivilege roleToPrivilege :userCredentialToRole.getRole().getRoleToPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(roleToPrivilege.getPrivilege().getPrivilegeName()));
            }
        }

        customUserDetails.setUser(user);
        customUserDetails.setUserCredential(user.getUserCredential());
        customUserDetails.setAuthorities(authorities);

        return customUserDetails;
    }
}
