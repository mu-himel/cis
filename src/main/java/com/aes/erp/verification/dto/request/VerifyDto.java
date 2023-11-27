package com.aes.erp.verification.dto.request;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.verification.enums.DomainType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyDto {
    ReferenceObjectDto verifier;
    Long domainId;
    DomainType domainType;
    String comment;
    ReferenceObjectDto reviewer;
}
