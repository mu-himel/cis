//package com.aes.erp.demand.dto.response;
//
//import com.aes.erp.demand.enums.DemandStatus;
//import com.aes.erp.verification.entity.Comment;
//import com.aes.erp.verification.entity.Verification;
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class DemandDetailResDto {
//    String demandNo;
//    Long demandId;
//    LocalDateTime demandDate;
//    DemandStatus demandStatus;
//    List<DemandDetailItemResDto> details;
//
//    Long empId;
//    String employeeId;
//    String employeeName;
//    String reportingManager;
//    String department;
//    String designation;
//
//    List<?> verifiers;
//    List<?> approvers;
//    List<?> comments;
//
//    public void addDetail(DemandDetailItemResDto demandDetailItemResDto){
//        if(this.details!=null){
//            this.details.add(demandDetailItemResDto);
//        }else{
//            this.details = new ArrayList<>();
//            this.details.add(demandDetailItemResDto);
//        }
//    }
//
//    public void setComments(List<?> comments) {
//        this.comments = comments;
//    }
//}
