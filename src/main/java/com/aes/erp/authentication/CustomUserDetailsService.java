package com.aes.erp.authentication;

import com.aes.erp.authentication.dto.UserInfoDto;
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
import com.aes.erp.vendor.entity.Vendor;
import com.aes.erp.vendor.service.VendorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final VendorService vendorService;

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
            if(roleName.equals("EMPLOYEE")||roleName.equals("INVENTORY CONTROLLER")
            ||roleName.equals("AUDITOR") || roleName.equals("ENLISTER")){
               Optional<Employee> employeeOptional = employeeService.getEmployeeByUserId(user.getId());
               if(employeeOptional.isPresent()){
                    customUserDetails.setEmployee(employeeOptional.get());
               }
            }
            if(roleName.equals("VENDOR")){
                Optional<Vendor> vendor = vendorService.getVendorByUserId(user.getId());
                if(vendor.isPresent()){
                    customUserDetails.setVendor(vendor.get());
                }
            }
            if(roleName.equals("SYS_ADMIN")){
                UserInfoDto userInfoDto = new UserInfoDto();
                userInfoDto.setId(user.getId());
                userInfoDto.setName(user.getFirstName()+ " " + user.getLastName());
                customUserDetails.setUserInfoDto(userInfoDto);
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
    public Vendor getLoggedInVendor(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getVendor();
        } else {
            throw new RuntimeException("Error occurred in getting the logged in vendor");
        }
    }
}
