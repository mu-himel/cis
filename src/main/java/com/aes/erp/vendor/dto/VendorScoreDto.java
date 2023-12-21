package com.aes.erp.vendor.dto;

import lombok.Data;

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
}
