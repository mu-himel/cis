package com.aes.erp.inventory.dto.response;

import lombok.Data;

@Data
public class KeycloakOauth2Dto {
    String access_token;
    int expires_in;
    int refresh_expires_in;
    String refresh_token;
    String token_type;
    String scope;
}
