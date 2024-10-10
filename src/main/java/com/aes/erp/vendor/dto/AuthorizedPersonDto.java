package com.aes.erp.vendor.dto;

import lombok.Data;

@Data
public class AuthorizedPersonDto {
    private Long id;
    private String name;
    private Long phoneNumber;
    private String nid;
    private String letter;
}
