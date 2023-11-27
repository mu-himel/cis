package com.aes.erp.user_management.user_credential.dto;

import com.aes.erp.user_management.user_credential.annotation.ValidPassword;
import com.aes.erp.user_management.user_credential.entity.UserCredential;
import com.aes.erp.config.RegexPattern;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserCredentialDTO {

    private static final long serialVersionUID = 1L;

    private long id;

    @Pattern(regexp = RegexPattern.EMAIL_PATTERN, message = "Email id is invalid")
    @NotBlank(message = "Email is mandatory")
    private String emailAddress;

    @ValidPassword
    private String password;

    private boolean active;

    private UserCredentialToRoleDTO userCredentialToRoleDTO;

    /*private String roles;*/

    public UserCredentialDTO from(UserCredential userCredential) {
        UserCredentialDTO dto = new UserCredentialDTO();
        dto.setId(userCredential.getId());
        dto.setEmailAddress(userCredential.getEmailAddress());
        dto.setPassword(userCredential.getPassword());
        dto.setActive(userCredential.isActive());
        return dto;
    }

    public UserCredential to(UserCredentialDTO dto) {
        UserCredential userCredential = new UserCredential();
        userCredential.setId(dto.getId());
        userCredential.setEmailAddress(dto.getEmailAddress());
        userCredential.setPassword(dto.getPassword());
        userCredential.setActive(dto.isActive());
        return userCredential;
    }
}
