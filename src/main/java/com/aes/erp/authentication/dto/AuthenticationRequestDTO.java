package com.aes.erp.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class documentation Comments to be added
 * */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDTO {

    private String username;
    private String password;

}
