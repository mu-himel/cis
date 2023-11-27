package com.aes.erp.verification.dto.request;

import com.aes.erp.common.ReferenceObjectDto;
import com.aes.erp.employee.entity.Employee;
import com.aes.erp.verification.enums.DomainType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;

    private Long domainId;

    private DomainType domainType;

    @ApiModelProperty(value = "Employee Object Ref with ID")
    private ReferenceObjectDto commentedBy;

    private String message;
}
