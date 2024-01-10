package com.aes.erp.vendor.dto;

import com.aes.erp.employee.enums.EmployeeType;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class VendorScoreDto {
    private Long id;
    private Float yearOfEstablishmentWeight;
    private Float yearOfEstablishmentGrade;
    private Float legalDocumentationWeight;
    private Float legalDocumentationGrade;
    private Float typeOfBusinessWeight;
    private Float typeOfBusinessGrade;
    private Float clientListAndCustomerReferenceWeight;
    private Float clientListAndCustomerReferenceGrade;
    private Float organizationWeight;
    private Float organizationGrade;
    private Float noOfEmployeeWeight;
    private Float noOfEmployeeGrade;
    private Float capacityWeight;
    private Float capacityGrade;
    private Float physicalVerificationWeight;
    private Float physicalVerificationGrade;
    private Float relevantExperienceWeight;
    private Float relevantExperienceGrade;
    private Float totalScore;

    private Integer aitPercentage;
    private EmployeeType employeeType;

    public void setEmployeeType(String employeeType) {
        this.employeeType = EmployeeType.valueOf(employeeType);
    }
}
