package com.aes.erp.module_access.approval_setting.controller;

import com.aes.erp.module_access.approval_setting.dto.request.ApprovalSettingDto;
import com.aes.erp.module_access.approval_setting.service.ApprovalSettingService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/approval-settings")
public class ApprovalSettingController {

    @Autowired
    private ApprovalSettingService approvalSettingService;

    @PostMapping
    @ApiOperation(value = "Create Default Approval Settings")
    public ResponseEntity<?> addDefaultSetting(@RequestBody List<ApprovalSettingDto> approvalSettingDto){
        approvalSettingService.createApprovalSettings(approvalSettingDto);
        return new ResponseEntity<>(HttpStatus.CREATED);

    }

    @GetMapping("/default")
    @ApiOperation(value = "Get Default Approval Settings")
    public ResponseEntity<?> getDefaultApprovalSetting(){
        return new ResponseEntity<>(approvalSettingService.getDefaultApprovalSetting(), HttpStatus.OK);
    }

    @GetMapping
    @ApiOperation(value = "Get Approval Panels by Module Uri")
    public ResponseEntity<?> getApprovalPanel(
            @RequestHeader("uri") String uri,
            @RequestParam("categoryId") Optional<Long> categoryId,
            @RequestParam("amount") Optional<BigDecimal> amount
            ){
        List<?> result = approvalSettingService.getModuleWiseApprovalSetting(uri,categoryId,amount);

        return new ResponseEntity<>(
                result,HttpStatus.OK
        );
    }

    @GetMapping("/module-wise")
    public ResponseEntity<?> getApprovalPanel(
            @RequestHeader("uri") String uri
    ){
        List<?> result = approvalSettingService.getModuleWiseApprovalSetting(uri);

        return new ResponseEntity<>(
                result,HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteApprovalSetting(@PathVariable("id") Long id){
        approvalSettingService.deleteApprovalSetting(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @DeleteMapping("/option/{id}")
    @ApiOperation(value = "Delete Approval Setting Option")
    public ResponseEntity<?> deleteApprovalSettingOption(@PathVariable("id") Long id){
        approvalSettingService.deleteApprovalSettingOption(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


}
