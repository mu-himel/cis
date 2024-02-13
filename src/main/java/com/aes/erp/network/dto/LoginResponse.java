package com.aes.erp.network.dto;

import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String jwt;
    private Long id;
    private List<String> roles;
    private String status;
}
