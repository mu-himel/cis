package com.aes.erp.vendor.document_response_dto;

import com.aes.erp.vendor.entity.DocmentEntities.BankSolvencyDocument;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankSolvencyDto {

    @JsonIgnore
    private String ID;
    @JsonIgnore
    private String secret_key;
    @JsonProperty("accountNumber")
    private String accountNumber;
    @JsonProperty("branchName")
    private String branchName;
    @JsonProperty("bankName")
    private String bankName;
    @JsonProperty("routingNumber")
    private String routingNumber;
    @JsonProperty("Error")
    private String error;


    public BankSolvencyDocument dtoToEntityMapping(BankSolvencyDto dto, BankSolvencyDocument bankSolvencyDocument){
        if(!dto.getAccountNumber().isEmpty())bankSolvencyDocument.setAccount(dto.getAccountNumber());
        if(!dto.getBankName().isEmpty())bankSolvencyDocument.setBankName(dto.getBankName());
        if(!dto.getBranchName().isEmpty())bankSolvencyDocument.setBranchName(dto.getBranchName());
        return bankSolvencyDocument;
    }
}
