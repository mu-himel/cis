package com.aes.erp.vendor.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class EmailConfig {

    private String address;

    private String username;

    private String password;
}
