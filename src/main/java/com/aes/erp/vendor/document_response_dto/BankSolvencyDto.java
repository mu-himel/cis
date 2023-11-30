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
    @JsonProperty("ID")
    private String ID;
    @JsonIgnore()
    private String secret_key;
    @JsonProperty("Account No")
    private String account;
    @JsonProperty("Branch")
    private String branchName;
    @JsonProperty("Name of Bank")
    private String bankName;
    @JsonProperty("Routing No")
    private String routingNo;


    public BankSolvencyDocument dtoToEntityMapping(BankSolvencyDto dto, BankSolvencyDocument bankSolvencyDocument){
        if(!dto.getAccount().isEmpty())bankSolvencyDocument.setAccount(dto.getAccount());
        if(!dto.getBankName().isEmpty())bankSolvencyDocument.setBankName(dto.getAccount());
        if(!dto.getBranchName().isEmpty())bankSolvencyDocument.setBranchName(dto.getAccount());
        return bankSolvencyDocument;
    }
}
