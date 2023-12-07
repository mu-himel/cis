package com.aes.erp.vendor.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VendorRegistrationMailSender {
    private List<String> to = new ArrayList<>();
//    private List<String> cc;
//    private List<String> bcc;
    private String subject = "Vendor Registration Process Initiated";
    private String content = "Welcome to Vendor Portal! Please Upload your documents to get verified.";
    private boolean isHtml = true;

    public VendorRegistrationMailSender(String to) {
        this.to.add(to);
    }
}
