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
    @JsonProperty("account_no")
    private String accountNumber;
    @JsonProperty("branch_name")
    private String branchName;
    @JsonProperty("bank_name")
    private String bankName;
    @JsonProperty("routing_number")
    private String routingNumber;
    @JsonProperty(value = "date")
    private String date;
    @JsonProperty(value = "name")
    private String name;
    //    @JsonProperty("Error")
//    private String error;
    @JsonProperty("state")
    private String error;
    @JsonProperty("message")
    private String message;

    public BankSolvencyDocument dtoToEntityMapping(BankSolvencyDto dto, BankSolvencyDocument bankSolvencyDocument) {
        if (!dto.getAccountNumber().isEmpty()) bankSolvencyDocument.setAccount(dto.getAccountNumber());
        if (!dto.getBankName().isEmpty()) bankSolvencyDocument.setBankName(dto.getBankName());
        if (!dto.getBranchName().isEmpty()) bankSolvencyDocument.setBranchName(dto.getBranchName());
        return bankSolvencyDocument;
    }
}
