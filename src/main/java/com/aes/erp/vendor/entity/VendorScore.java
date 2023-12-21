package com.aes.erp.vendor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class VendorScore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
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
    private Float relevantExperienceWeight;
    private Float relevantExperienceGrade;
    private Float physicalVerificationWeight;
    private Float physicalVerificationGrade;
    private Float totalScore = 0F;
}
