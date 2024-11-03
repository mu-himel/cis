package com.aes.erp.vendor.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "ml.api")
public class MLApiConfig {
    // public String tin;
    // public String bin;
    // public String nid;
    public String solvency;
    public String trade;
    public String apiEndpoint;
    public String apiEndpoint_dev_cluster;
}
