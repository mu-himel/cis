package com.aes.erp.user_management.user_credential.controller;

import com.aes.erp.user_management.user_credential.dto.ForgotPasswordDTO;
import com.aes.erp.user_management.user_credential.dto.PasswordResetTokenDTO;
import com.aes.erp.user_management.user_credential.dto.UserCredentialDTO;
import com.aes.erp.user_management.user_credential.service.UserCredentialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Class documentation Comments to be added
 * */
@Slf4j
@RestController
@RequiredArgsConstructor
public class UserCredentialController {
    private final UserCredentialService userCredentialService;

    private static final Logger logger = LoggerFactory.getLogger(UserCredentialController.class);

    @PostMapping("/users-credential")
    public ResponseEntity<?> saveCredential(@RequestBody @Valid PasswordResetTokenDTO passwordResetTokenDTO) {

        userCredentialService.setPassword(passwordResetTokenDTO);
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/users/reset-password")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN', 'ROLE_LEAD_ENGR', 'ROLE_SOP_ENGR', 'ROLE_USER')")
    public ResponseEntity<?> updateCredential(@Valid @RequestBody UserCredentialDTO userCredentialDTO) {

         userCredentialService.update(userCredentialDTO.to(userCredentialDTO));
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /** During login */
    @PostMapping("/users/verify-credential")
    @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN', 'ROLE_LEAD_ENGR', 'ROLE_SOP_ENGR', 'ROLE_USER')")
    public ResponseEntity<?> verifyCredential(@RequestBody UserCredentialDTO userCredentialDTO) {
        userCredentialService.verifyPassword(userCredentialDTO);
//        APIResponse apiResponse =
//        return apiResponse.isSuccess() ? ok(apiResponse) : badRequest().body(apiResponse);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/forgot-password")
    // @PreAuthorize("hasAnyAuthority('ROLE_SYS_ADMIN', 'ROLE_LEAD_ENGR', 'ROLE_SOP_ENGR', 'ROLE_USER')")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordDTO forgotPasswordDTO) {

        userCredentialService.changePassword(forgotPasswordDTO.getEmailAddress());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}