package com.aes.erp.user_management.dto;

import com.aes.erp.user_management.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private long id;
    private String firstName;
    private String lastName;
    private String emailAddress;
    private String roles;
    private String organizations;
    private Boolean status;
    private String organizationFiles;

    public static UserResponseDTO getUserResponseDTO(User user){
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(user.getId());
        userResponseDTO.setFirstName(user.getFirstName());
        userResponseDTO.setLastName(user.getLastName());
        userResponseDTO.setEmailAddress(user.getEmailAddress());
        userResponseDTO.setRoles(user.getUserCredential().getUserCredentialToRoles().stream().map(userCredentialToRole -> {
            if(userCredentialToRole == null) return null;
            return userCredentialToRole.getRole().getRoleName();
        }
        ).collect(Collectors.joining(",")));
//        userResponseDTO.setOrganizations(user.getUserToOrganizations().stream().map(userToOrganization -> {
//            if(userToOrganization == null) return null;
//             return userToOrganization.getOrganization().getOrganizationProfile().getName();
//        }).collect(Collectors.joining(",")));

//        if (user.getUserToOrganizationFiles() != null)
//            userResponseDTO.setOrganizationFiles(user.getUserToOrganizationFiles().stream().map(
//                    userToOrganizationFile -> {
//                        if(userToOrganizationFile == null) return null;
//                        return userToOrganizationFile.getOrganizationFile().getName();
//                    }
//            ).collect(Collectors.joining(",")));

        userResponseDTO.setStatus(user.getUserCredential().isActive());
        return userResponseDTO;
    }
}
