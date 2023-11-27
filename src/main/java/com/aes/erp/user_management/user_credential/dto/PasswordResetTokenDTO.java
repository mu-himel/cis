package com.aes.erp.user_management.user_credential.dto;

import com.aes.erp.user_management.user_credential.annotation.ValidPassword;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PasswordResetTokenDTO {

    @NotBlank(message = "Token field is empty")
    private String token;

    @ValidPassword
    private String password;
}
