package com.aes.erp.verification.service;

import com.aes.erp.authentication.dto.EmployeeInfoDto;
import com.aes.erp.verification.dto.response.Verifier;

import java.util.List;

public interface VerifierPanelService {

    List<Verifier> getVerifierPanel(EmployeeInfoDto employeeInfo, Integer fromLevel, Integer toLevel);
}
