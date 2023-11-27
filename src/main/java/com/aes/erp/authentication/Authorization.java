package com.aes.erp.authentication;

import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.service.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Class documentation Comments to be added
 * */
@Service
@RequiredArgsConstructor
public class Authorization {

    private final UserRepository userRepository;
//    private final OrganizationRepository organizationRepository;

    Logger logger = LoggerFactory.getLogger(Authorization.class);

    public boolean isAuthorized(User user) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isSameUser = user.getEmailAddress().equals(userDetails.getUsername());
        boolean isSysAdmin = userDetails.getAuthorities().stream().toList().contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"));
        if (!isSameUser && !isSysAdmin) {
            return false;
        }
        else return true;
    }

//    public boolean isAuthorizedToAccessTeam(long teamId) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
//        if(userDetails.getAuthorities().stream().toList().contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"))) return true;
//        else {
//            if(user.getUserToTeams().stream().map(userToTeam -> userToTeam.getTeam().getId()).collect(Collectors.toList()).contains(teamId)) {
//                return true;
//            }
//            return false;
//        }
//
//    }

//    public boolean isAuthorizedToAccessOrganization(long organizationId) {
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        User user = userRepository.findByEmailAddress(userDetails.getUsername()).orElse(null);
//        if(userDetails.getAuthorities().stream().toList().contains(new SimpleGrantedAuthority("ROLE_SYS_ADMIN"))) return true;
//        else {
//            if(Objects.nonNull(user.getUserToTeams())) {
//                for(UserToTeam userToTeam : user.getUserToTeams()) {
//                    long id = organizationRepository.findByTeamId(userToTeam.getTeam().getId());
//                    if(id == organizationId) return true;
//                }
//                return false;
//
//            }
//            if(Objects.nonNull(user.getUserToOrganizations())) {
//                for(UserToOrganization userToOrganization : user.getUserToOrganizations()) {
//                    if(userToOrganization.getOrganization().getId() == organizationId)
//                        return true;
//                }
//                return false;
//            }
//            return false;
//        }
//    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        return userRepository.findByEmailAddress(currentUserEmail).orElse(null);
    }
}
