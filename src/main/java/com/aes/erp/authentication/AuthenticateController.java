package com.aes.erp.authentication;

import com.aes.erp.authentication.entity.CustomUserDetails;
import com.aes.erp.exception.AesException;
import com.aes.erp.authentication.dto.AuthenticationRequestDTO;
import com.aes.erp.authentication.dto.AuthenticationResponseDTO;
import com.aes.erp.user_management.entity.User;
import com.aes.erp.user_management.entity.UserCredentialToRole;
import com.aes.erp.user_management.service.UserRepository;
//import com.aes.erp.user_management.service.UserToOrganizationFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class documentation Comments to be added
 * */
@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticateController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticateController.class);

    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailsService userDetailsService;

    private final UserRepository userRepository;

    private final JwtUtil jwtTokenUtil;

//    private APIResponse apiResponse = getApiResponse();

//    private final UserToOrganizationFileRepository userToOrganizationFileRepository;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequestDTO authenticationRequestDTO) throws Exception {
        /** Step 1: Authenticating username, password */
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getUsername(), authenticationRequestDTO.getPassword()));
        } catch (BadCredentialsException e) {
            throw new AesException("Incorrect username or password.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("username: "+ authenticationRequestDTO.getUsername());
        logger.info("password: "+ authenticationRequestDTO.getPassword());

        /** Step 2: If authenticated then generating JWT to return as response. */
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(authenticationRequestDTO.getUsername());
        User user = userRepository.findByEmailAddress(authenticationRequestDTO.getUsername()).orElse(null);

        if(user.getUserCredential().isActive()) {
            final String jwt = jwtTokenUtil.generateToken(userDetails);

            /*List<UserToOrganizationFile> userToOrganizationFiles = userToOrganizationFileRepository.findByUserId(user.getId());
            if (userToOrganizationFiles != null) {
                System.out.println("1===>" + userToOrganizationFiles.size());
            }*/
//            System.out.println("===>" + user.getUserCredential().getPassword());
            // System.out.println("===>" + user.getUserToOrganizations());
            // System.out.println("===>" + user.getUserToOrganizationFiles());
            Long vendorId = null;
            if(userDetails.getVendor() != null) vendorId = userDetails.getVendor().getId();
            AuthenticationResponseDTO authenticationResponseDTO =new AuthenticationResponseDTO(jwt, user.getId(),
                    getRoles(user.getUserCredential().getUserCredentialToRoles()),
                    //getOrganizations(user.getUserToOrganizations()),
                    Collections.emptyList(),
                    // getOrganizationFiles(user.getUserToOrganizationFiles()) /*Collections.emptyList()*/,
                    vendorId, user.getUserCredential().isActive()?"active":"deactive");

            authenticationResponseDTO.setEmployee(userDetails.getEmployee());
            return new ResponseEntity<>(authenticationResponseDTO, HttpStatus.OK);
        }
        else {
            return ResponseEntity.ok(
                    new AuthenticationResponseDTO("", 0,
                            Collections.emptyList(),
                            Collections.emptyList(),
                            null, "deactive")
            );
            // apiResponse.setResponse("this account is deactivated", TRUE, NULL, SUCCESS);
            // return ResponseEntity.ok().body(apiResponse);
        }


    }
    private List<String> getRoles(List<UserCredentialToRole> userCredentialToRoles) {
        List<String> roles = new ArrayList<>();
        for(UserCredentialToRole userCredentialToRole : userCredentialToRoles) {
            roles.add(userCredentialToRole.getRole().getRoleName());
            logger.info("roleName: " + userCredentialToRole.getRole().getRoleName());
            logger.info("roles" + roles);
        }
        return roles;

    }

//    private List<OrganizationInfo> getOrganizations(List<UserToOrganization> userToOrganizations) {
//        List<OrganizationInfo> organizationInfos = new ArrayList<>();
//        for (UserToOrganization userToOrganization : userToOrganizations) {
//            OrganizationInfo organizationInfo = new OrganizationInfo();
//            if(Objects.nonNull(userToOrganization.getOrganization())) {
//                organizationInfo.setId(userToOrganization.getOrganization().getId());
//                organizationInfo.setOrganizationName(userToOrganization.getOrganization().getOrganizationProfile().getName());
//            }
//            organizationInfos.add(organizationInfo);
//        }
//        return organizationInfos;
//
//    }

//    private List<OrganizationFileResponseDTO> getOrganizationFiles(List<UserToOrganizationFile> userToOrganizationFiles) {
//        List<OrganizationFileResponseDTO> organizationFileResponseDTOS = new ArrayList<>();
//        for (UserToOrganizationFile userToOrganizationFile : userToOrganizationFiles) {
//            OrganizationFileResponseDTO organizationFileResponseDTO = new OrganizationFileResponseDTO();
//
//            if(Objects.nonNull(userToOrganizationFile.getOrganizationFile())) {
//                organizationFileResponseDTO.getResponseDTO(userToOrganizationFile.getOrganizationFile());
//            }
//            organizationFileResponseDTOS.add(organizationFileResponseDTO);
//        }
//        return organizationFileResponseDTOS;
//
//    }
}