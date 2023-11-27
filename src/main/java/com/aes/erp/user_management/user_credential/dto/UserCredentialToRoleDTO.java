package com.aes.erp.user_management.user_credential.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCredentialToRoleDTO {
    private long userCredentialId;

    private long roleId;


}
