package com.aes.erp.demand.dto.request;

import com.aes.erp.verification.enums.DomainType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    DomainType domainType;
    String message;
}
