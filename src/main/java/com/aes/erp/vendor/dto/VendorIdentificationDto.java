package com.aes.erp.vendor.dto;

import lombok.Data;

@Data
public class VendorIdentificationDto {
    private String nid;
    private String tin;
    private String bin;
    private String trade;
    private String solvency;
    private byte[] nidFile;
    private byte[] tinFile;
    private byte[] binFile;
    private byte[] tradeFile;
    private byte[] solvencyFile;
    private String nidContentType;
    private String binContentType;
    private String tinContentType;
    private String tradeContentType;
    private String solvencyContentType;
}
