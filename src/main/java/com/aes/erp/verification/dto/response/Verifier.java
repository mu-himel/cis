package com.aes.erp.verification.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EntityResult;
import javax.persistence.SqlResultSetMapping;

@Data
@NoArgsConstructor
public class Verifier {
    Long id;
    String name;
    Boolean verified;
    Long departmentId;
    String departmentName;
    Integer departmentLevel;

    Long designationId;

    String designation;

    public Verifier(Long id, String name, Boolean verified,
                    Long departmentId, String departmentName, Integer departmentLevel,
                    Long designationId, String designation) {

        this.id = id;
        this.name = name;
        this.verified = verified;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentLevel = departmentLevel;
        this.designationId = designationId;
        this.designation = designation;
    }
}
