//package com.aes.erp.user_management.entity;
//
//import com.aes.erp.user_management.user_credential.entity.UserCredential;
//import com.fasterxml.jackson.annotation.JsonIdentityInfo;
//import com.fasterxml.jackson.annotation.ObjectIdGenerators;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Data
//@JsonIdentityInfo(generator= ObjectIdGenerators.PropertyGenerator.class, property="id")
//@Entity
//@NoArgsConstructor
//@AllArgsConstructor
//public class UserCredentialToPrivilege {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private long id;
//    @ManyToOne(cascade = CascadeType.ALL)
//    private UserCredential userCredential;
//    @ManyToOne(cascade = CascadeType.ALL)
//    private Privilege privilege;
//}
